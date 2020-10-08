package com.neu.edu.user.service;

import com.neu.edu.user.modal.Answer;
import com.neu.edu.user.modal.Category;
import com.neu.edu.user.modal.Question;
import com.neu.edu.user.modal.User;
import com.neu.edu.user.repository.AnswerRepository;
import com.neu.edu.user.repository.CategoryRepository;
import com.neu.edu.user.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AnswerRepository answerRepository;

    public Question addQuestion(Question question, User user) throws Exception {
        String date = String.valueOf(java.time.LocalDateTime.now());
        question.setUpdated_timestamp(date);
        if(question.getCreated_timestamp()==null)
            question.setCreated_timestamp(date);
        question.setUserId(user.getUserId());
        List<Category> categoryList = new ArrayList<Category>();
        List<String> categories= new ArrayList<String>();
        if(question.getCategories()!=null&&question.getCategories().size()>0){
            for(Category cat:question.getCategories()){
                String catLower=cat.getCategory().toLowerCase().trim();
                if(catLower.equals(""))
                    throw new Exception();
                if(!categories.contains(catLower)){
                    Category existingCat= categoryRepository.findByCategory(catLower);
                    if(existingCat==null){
                        cat.setCategory(catLower);
                        existingCat=cat;
                        existingCat=categoryRepository.save(existingCat);
                    }
                    categories.add(catLower);
                    categoryList.add(existingCat);
                }
            }
            question.setCategories(null);
            question.setCategories(categoryList);
        }
        /*Set<Category> cat=question.getCategories();
        Set<Category> existingCat= (Set<Category>) categoryRepository.findAll();
        for(Category c : cat){
            question.getCategories().add(c);
        }*/
            /*List<Category> newCategories = question.getCategories();
        question.setCategories(null);
        List<Category> categoryList = new ArrayList<>();
            newCategories.stream().filter(distinctByKey(c->c.getCategory())).forEach((category -> {
                String categoryName = category.getCategory().toLowerCase().trim();
                if(categoryName==null||categoryName.trim().isEmpty())
                    System.out.println("null");
                Category c = categoryRepository.findByCategory(categoryName);
                    if(null!=c)
                        categoryList.add(c);
                    else{
                        Category addCategory = new Category();
                        addCategory.setCategory(categoryName);
                        categoryList.add(categoryRepository.save(addCategory));
                    }
            }));
            question.setCategories(categoryList);*/
            return questionRepository.save(question);
    }

    public Question getQuestionById(String id){
        return questionRepository.findById(id).orElse(null);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Object deleteQuestionById(User user, String question_id) throws Exception {
        Question existingQuestion = questionRepository.findById(question_id).orElse(null);
        System.out.println("questtttt");
        if(existingQuestion==null)
            return new ResponseEntity<>("Question Not found", HttpStatus.NOT_FOUND);
        if(!(user.getUserId().equals(existingQuestion.getUserId())))
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        if(existingQuestion.getAnswers().size()>0){
            return new ResponseEntity<>("Answer exists - could not delete", HttpStatus.BAD_REQUEST);
        }
        else{
            questionRepository.deleteById(question_id);
            return new ResponseEntity<>("Not content", HttpStatus.NO_CONTENT);
        }

        /*if(!(user.getUserId().equals(existingQuestion.getUserId())))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised");
        if(existingQuestion.getAnswers().size()!=0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");*/

    }

    public Object updateQuestionById(User user, String question_id, Question question) throws Exception {
        Question existingQuestion = questionRepository.findById(question_id).orElse(null);
        if(existingQuestion==null)
            return new ResponseEntity<>("Question Not found", HttpStatus.NOT_FOUND);
        if(!(user.getUserId().equals(existingQuestion.getUserId())))
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        existingQuestion.setQuestion_text(question.getQuestion_text());
        String date = String.valueOf(java.time.LocalDateTime.now());
        existingQuestion.setUpdated_timestamp(date);
        List<Category> categoryList = new ArrayList<Category>();
        List<String> categories= new ArrayList<String>();
        if(question.getCategories()!=null&&question.getCategories().size()>0){
            for(Category cat:question.getCategories()){
                String catLower=cat.getCategory().toLowerCase().trim();
                if(!categories.contains(catLower)){
                    Category existingCat= categoryRepository.findByCategory(catLower);
                    if(existingCat==null){
                        cat.setCategory(catLower);
                        existingCat=cat;
                        existingCat=categoryRepository.save(existingCat);
                    }
                    categories.add(catLower);
                    categoryList.add(existingCat);
                }
            }
            question.setCategories(null);
            question.setCategories(categoryList);
        }
        existingQuestion.setCategories(question.getCategories());
        questionRepository.save(existingQuestion);
        return new ResponseEntity<>("No Content", HttpStatus.NO_CONTENT);
    }
}
