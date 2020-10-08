package com.neu.edu.user.controller;

import com.neu.edu.user.modal.Answer;
import com.neu.edu.user.modal.Question;
import com.neu.edu.user.modal.User;
import com.neu.edu.user.service.AnswerService;
import com.neu.edu.user.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value="/v1")
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;

    @GetMapping(value="/question/{question_id}/answer/{answer_id}")
    public Object getAnswer(@PathVariable String question_id, @PathVariable String answer_id){
        try{
            Answer answer = answerService.getAnswer(answer_id);
            if(answer==null)
                throw new Exception();
            else
                return answer;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found",e);
        }
    }

    @GetMapping(value="/question/{question_id}")
    public Object getQuestionById(@PathVariable String question_id){
        try{
            Question question = questionService.getQuestionById(question_id);
            if(question==null)
                throw new Exception();
            else
                return question;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found",e);
        }
    }

    @GetMapping(value = "/questions")
    public List<Question> getAllQuestions(){
        return questionService.getAllQuestions();
    }

    @PutMapping(value = "/question/{question_id}/answer/{answer_id}")
    public Object updateAnswerById(@AuthenticationPrincipal User user,@RequestBody Answer answer, @PathVariable String question_id, @PathVariable String answer_id) throws ResponseStatusException {
        try{
            return answerService.updateAnswerById(user, answer_id, answer);
        }
        catch (Exception e){
            throw e;
        }
    }

    @PutMapping(value = "/question/{question_id}")
    public Object updateQuestionById(@AuthenticationPrincipal User user,@RequestBody Question question,@PathVariable String question_id){
        try{
            if(question.getQuestion_text().isEmpty()||question.getQuestion_text().equals(""))
                    return new ResponseEntity<>("Question text empty", HttpStatus.BAD_REQUEST);
            else{
                Object o= questionService.updateQuestionById(user, question_id, question);
                return o;
            }
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UnAuthorised",e);
        }
    }
    @PostMapping(value = "/question/{question_id}/answer")
    public Object addAnswer(@AuthenticationPrincipal User user,@RequestBody Answer answer, @PathVariable String question_id){
        System.out.println("answer"+answer.getAnswer_text());
        try{
            if(answer.getAnswer_text().isEmpty()||answer.getAnswer_text().equals(""))
                return new ResponseEntity<>("Answer text is empty", HttpStatus.BAD_REQUEST);
            Question question = questionService.getQuestionById(question_id);
            if(question==null)
                return new ResponseEntity<>("Question NOt Found", HttpStatus.NOT_FOUND);
            return answerService.addAnswer(question, answer, user);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request",e);
        }
    }

    @PostMapping(value = "/question")
    public Object addQuestion(@AuthenticationPrincipal User user, @RequestBody Question question) throws Exception {
        try{
            if(question.getQuestion_text().isEmpty())
                return new ResponseEntity<>("Question is empty", HttpStatus.BAD_REQUEST);
            return questionService.addQuestion(question, user);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Found",e);
        }
    }

    @DeleteMapping(value = "/question/{question_id}/answer/{answer_id}")
    public Object deleteAnswerById(@AuthenticationPrincipal User user, @PathVariable String question_id, @PathVariable String answer_id){
        Object o=answerService.deleteAnswerById(user,question_id,answer_id);
        return o;
    }

    @DeleteMapping(value = "/question/{question_id}")
    public Object deleteQuestionById(@AuthenticationPrincipal User user, @PathVariable String question_id){
        try{
            System.out.println("queson");
            Object o=questionService.deleteQuestionById(user, question_id);
            return o;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Found",e);
        }
    }
}
