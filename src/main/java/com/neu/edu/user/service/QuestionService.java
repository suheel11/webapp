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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AnswerRepository answerRepository;

    public Question addQuestion(Question question, User user){
        String date = String.valueOf(java.time.LocalDateTime.now());
        question.setUpdated_timestamp(date);
        if(question.getCreated_timestamp()==null)
            question.setCreated_timestamp(date);
        question.setUserId(user.getUserId());
        Set<Category> cat=question.getCategories();
        Set<Category> existingCat= (Set<Category>) categoryRepository.findAll();
        for(Category c : cat){
            question.getCategories().add(c);
        }
        return questionRepository.save(question);
    }

    public Question getQuestionById(String id){
        return questionRepository.findById(id).orElse(null);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public void deleteQuestionById(User user, String question_id) throws Exception {
        Question existingQuestion = questionRepository.findById(question_id).orElse(null);
        /*if(!(user.getUserId().equals(existingQuestion.getUserId())))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised");
        if(existingQuestion.getAnswers().size()!=0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");*/
        questionRepository.deleteById(question_id);
    }

    public Question updateQuestionById(User user, String question_id, Question question) throws Exception {
        Question existingQuestion = questionRepository.findById(question_id).orElse(null);
        if(!(user.getUserId().equals(existingQuestion.getUserId())))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised");
        existingQuestion.setQuestion_text(question.getQuestion_text());
        String date = String.valueOf(java.time.LocalDateTime.now());
        existingQuestion.setUpdated_timestamp(date);
        return questionRepository.save(existingQuestion);
    }
}
