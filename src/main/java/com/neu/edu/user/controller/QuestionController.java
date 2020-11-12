package com.neu.edu.user.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.neu.edu.user.modal.*;
import com.neu.edu.user.service.AnswerService;
import com.neu.edu.user.service.QuestionService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.*;

@Configuration
@RestController
@RequestMapping(value="/v1")
public class QuestionController implements EnvironmentAware {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;

    private Environment env;
    @Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

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

    @PostMapping(value="/question/{question_id}/file",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFile(@AuthenticationPrincipal User loggedUser, @PathVariable String question_id , @RequestParam(value = "file") MultipartFile file){

        Question q = questionService.getQuestionById(question_id);
        if(q==null)
            return new ResponseEntity<>("Id not found",HttpStatus.NOT_FOUND);
        if(!loggedUser.getUserId().equals(q.getUserId()))
            return new ResponseEntity<>("User Cannot Update/delete question",HttpStatus.UNAUTHORIZED);
        String accessKey=env.getProperty("aws-access-key-id");
        String secretKey=env.getProperty("aws-secret-access-key");
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,secretKey);
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(creds);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true).build();
        String bucket_name = "webapp.suheel.vallamkonda";
        UUID uuid = UUID.randomUUID();
        String keyName = question_id+"/"+uuid.toString()+"/"+file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(!extension.equals("png")&&!extension.equals("jpg")&&!extension.equals("jpeg")){
            return new ResponseEntity<>("Invalid Image Type",HttpStatus.BAD_REQUEST);
        }
        try{
            QuestionFiles f = new QuestionFiles();
            f.setFileId(uuid.toString());
            f.setUserId(loggedUser.getUserId());
            f.setMime(extension);
            f.setQuestionId(question_id);
            f.setFileName(file.getName());
            f.setSize(String.valueOf(file.getSize()));
            f.setS3objectName(keyName);
            QuestionFiles output = questionService.saveFile(f);
            List<QuestionFiles> l = new ArrayList<>();
                l.add(f);
                q.setFiles(l);
                questionService.updateQuestionById(loggedUser,question_id,q);
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            s3.putObject(bucket_name,keyName,convFile);
            return output;

        }catch(AmazonServiceException | IOException e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value="/question/{question_id}/file/{file_id}")
    public Object deleteFile(@AuthenticationPrincipal User loggedUser, @PathVariable String question_id, @PathVariable String file_id ){
        QuestionFiles files = questionService.getFile(file_id);
        if(files==null)
            return new ResponseEntity<>("Id not found",HttpStatus.NOT_FOUND);
        if(!loggedUser.getUserId().equals(files.getUserId()))
            return new ResponseEntity<>("User Cannot Update/delete question",HttpStatus.UNAUTHORIZED);
        String accessKey=env.getProperty("aws-access-key-id");
        String secretKey=env.getProperty("aws-secret-access-key");
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,secretKey);
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(creds);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true).build();
        String bucket_name = "webapp.suheel.vallamkonda";
        try {
            s3.deleteObject(bucket_name, files.getS3objectName());
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        if(!files.getUserId().equals(loggedUser.getUserId()))
            return new ResponseEntity<>("Cannot Delete File",HttpStatus.UNAUTHORIZED);
        try {
            System.out.println(file_id);
            questionService.deleteFile(question_id, file_id);
            Question q2 = questionService.getQuestionById(question_id);
            List l= q2.getFiles();
            l.remove(files);
            questionService.updateQuestionById(loggedUser,question_id,q2);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Id not found",HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/question/{question_id}/answer/{answer_id}/file",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadAnswerFile(@AuthenticationPrincipal User loggedUser, @PathVariable String question_id ,@PathVariable String answer_id , @RequestParam(value = "file") MultipartFile file){
        Answer ans = answerService.getAnswer(answer_id);
        if(ans==null)
            return new ResponseEntity<>("Id Not Found",HttpStatus.NOT_FOUND);
        if(!ans.getUserId().equals(loggedUser.getUserId()))
            return new ResponseEntity<>("Cannot Upload File",HttpStatus.UNAUTHORIZED);
        String accessKey=env.getProperty("aws-access-key-id");
        String secretKey=env.getProperty("aws-secret-access-key");
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,secretKey);
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(creds);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true).build();
        String bucket_name = "webapp.suheel.vallamkonda";
        UUID uuid = UUID.randomUUID();
        String keyName = answer_id+"/"+uuid.toString()+"/"+file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if(!extension.equals("png")&&!extension.equals("jpg")&&!extension.equals("jpeg")){
            return new ResponseEntity<>("Invalid Image Type",HttpStatus.BAD_REQUEST);
        }
        try{
            AnswerFiles f = new AnswerFiles();
            f.setFileId(uuid.toString());
            f.setUserId(loggedUser.getUserId());
            f.setMime(extension);
            f.setAnswerId(answer_id);
            f.setFileName(file.getName());
            f.setSize(String.valueOf(file.getSize()));
            f.setS3objectName(keyName);
            AnswerFiles output = questionService.saveAnswerFile(f);
            List<AnswerFiles> l = new ArrayList<>();
            l.add(f);
            ans.setFiles(l);
            answerService.updateAnswerById(loggedUser,question_id,ans);
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();


            s3.putObject(bucket_name,keyName,convFile);
            return output;

        }catch(AmazonServiceException | IOException e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value="/question/{question_id}/answer/{answer_id}/file/{file_id}")
    public Object deleteAnswerFile(@AuthenticationPrincipal User loggedUser, @PathVariable String question_id,@PathVariable String answer_id, @PathVariable String file_id ){
        AnswerFiles files = questionService.getAnswerFile(file_id);
        if(files==null)
            return new ResponseEntity<>("Id Not Found",HttpStatus.NOT_FOUND);
        if(!files.getUserId().equals(loggedUser.getUserId()))
            return new ResponseEntity<>("Cannot Delete File",HttpStatus.UNAUTHORIZED);
        String accessKey=env.getProperty("aws-access-key-id");
        String secretKey=env.getProperty("aws-secret-access-key");
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,secretKey);
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(creds);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true).build();
        String bucket_name = "webapp.suheel.vallamkonda";
        try {
            s3.deleteObject(bucket_name, files.getS3objectName());
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
            return new ResponseEntity<>("Id not found",HttpStatus.NOT_FOUND);
        }
        if(!files.getUserId().equals(loggedUser.getUserId()))
            return new ResponseEntity<>("Cannot Delete File",HttpStatus.UNAUTHORIZED);
        try {
            System.out.println(file_id);
            Answer a2 = answerService.getAnswer(answer_id);
            List l= a2.getFiles();
            l.remove(files);
            answerService.updateAnswerById(loggedUser,answer_id,a2);
            questionService.deleteAnswerFile(answer_id, file_id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Id not found",HttpStatus.NOT_FOUND);
        }
    }
}
