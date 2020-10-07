package com.neu.edu.user.service;

import com.neu.edu.user.modal.Answer;
import com.neu.edu.user.modal.Question;
import com.neu.edu.user.modal.User;
import com.neu.edu.user.repository.AnswerRepository;
import com.neu.edu.user.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    public Answer getAnswer(String answer_id) {
       // Question question = questionRepository.findById(question_id).get();
        return answerRepository.findById(answer_id).orElse(null);
    }

    public Answer addAnswer(Question question, Answer answer, User user){
        System.out.println("service add answer");
        String date = String.valueOf(java.time.LocalDateTime.now());
        answer.setUpdated_timestamp(date);
        if(answer.getCreated_timestamp()==null){
            answer.setCreated_timestamp(date);
        }
        answer.setQuestionId(question.getQuestionId());
        answer.setUserId(user.getUserId());
        List<Answer> a = question.getAnswers();
        a.add(answer);
        question.setAnswers(a);
        return answerRepository.save(answer);
    }

    public Object updateAnswerById(User user, String answer_id, Answer answer) {
        try{
            Answer existingAnswer = answerRepository.findById(answer_id).orElse(null);
            if (existingAnswer == null)
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found");
            if (!(user.getUserId().equals(existingAnswer.getUserId())))
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
            existingAnswer.setAnswer_text(answer.getAnswer_text());
            String updateDate = String.valueOf(java.time.LocalDateTime.now());
            existingAnswer.setUpdated_timestamp(updateDate);
            return answerRepository.save(existingAnswer);
        }
        catch (Exception e){
            throw e;
        }
    }

    public void deleteAnswerById(String question_id, String answer_id) {
        Question question=questionRepository.findById(question_id).orElse(null);
        Answer answer=answerRepository.findById(answer_id).orElse(null);
        List<Answer> a = question.getAnswers();
        a.remove(answer);
        question.setAnswers(a);
        questionRepository.save(question);
        answerRepository.deleteById(answer_id);
    }
}
