package com.neu.edu.user.controller;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.neu.edu.user.modal.*;
import com.neu.edu.user.service.AnswerService;
import com.neu.edu.user.service.AmazonSNSClient;
import com.neu.edu.user.service.QuestionService;
import com.timgroup.statsd.NonBlockingStatsDClient;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


@RestController
@RequestMapping(value="/v1")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private AmazonSNSClient amazonSNSClient;

    @Autowired
    private AnswerService answerService;
    private final static Logger logger = LoggerFactory.getLogger(QuestionController.class);
    private final NonBlockingStatsDClient client = new NonBlockingStatsDClient("my.prefix", "localhost", 8125);

    @GetMapping(value="/question/{question_id}/answer/{answer_id}")
    public Object getAnswer(@PathVariable String question_id, @PathVariable String answer_id){
        try{
            logger.info("Method - Get answer");
            long startTime = System.currentTimeMillis();
            client.incrementCounter("/question/{question_id}/answer/{answer_id}");
            Answer answer = answerService.getAnswer(answer_id);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/question/{question_id}/answer/{answer_id}",duration);
            if(answer==null) {

                throw new Exception();
            }
            else
                return answer;
        }
        catch (Exception e){
            logger.error("Answer not found in database");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found",e);
        }
    }

    @GetMapping(value="/question/{question_id}")
    public Object getQuestionById(@PathVariable String question_id){
        try{
            logger.info("Method - Get question by id");
            long startTime = System.currentTimeMillis();
            client.incrementCounter("/question/{question_id}");
            Question question = questionService.getQuestionById(question_id);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/question/{question_id}",duration);
            if(question==null)
                throw new Exception();
            else
                return question;
        }
        catch (Exception e){
            logger.error("Question not found in database");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found",e);
        }
    }

    @GetMapping(value = "/questions")
    public List<Question> getAllQuestions(){
        logger.info("Method - Get questions");
        long startTime = System.currentTimeMillis();
        client.incrementCounter("/questions");
        List <Question> l =questionService.getAllQuestions();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        client.recordExecutionTime("/questions",duration);
        return l;
    }

    @PutMapping(value = "/question/{question_id}/answer/{answer_id}")
    public Object updateAnswerById(@AuthenticationPrincipal User user,@RequestBody Answer answer, @PathVariable String question_id, @PathVariable String answer_id) throws ResponseStatusException {
        try{
            logger.info("Method - Update answer by id");
            return answerService.updateAnswerById(user, answer_id, answer);
        }
        catch (Exception e){
            throw e;
        }
    }

    @PutMapping(value = "/question/{question_id}")
    public Object updateQuestionById(@AuthenticationPrincipal User user,@RequestBody Question question,@PathVariable String question_id){
        try{
            logger.info("Method - update question by id");
            long startTime = System.currentTimeMillis();
            client.incrementCounter("/question/{question_id}");
            if(question.getQuestion_text().isEmpty()||question.getQuestion_text().equals("")){
                logger.error("Question text empty");
                return new ResponseEntity<>("Question text empty", HttpStatus.BAD_REQUEST);
            }
            else{
                Object o= questionService.updateQuestionById(user, question_id, question);
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                client.recordExecutionTime("/question/{question_id}",duration);
                return o;
            }
        }
        catch (Exception e){
            logger.error("Unauthorised user");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UnAuthorised",e);
        }
    }
    @PostMapping(value = "/question/{question_id}/answer")
    public Object addAnswer(@AuthenticationPrincipal User user,@RequestBody Answer answer, @PathVariable String question_id){
        System.out.println("answer"+answer.getAnswer_text());
        try{
            logger.info("Method add answer");
            long startTime = System.currentTimeMillis();
            client.incrementCounter("/question/{question_id}/answer");
            if(answer.getAnswer_text().isEmpty()||answer.getAnswer_text().equals("")){
                logger.error("Answer text is empty");
                return new ResponseEntity<>("Answer text is empty", HttpStatus.BAD_REQUEST);
            }
            Question question = questionService.getQuestionById(question_id);
            if(question==null){
                logger.error("Question Not Found");
                return new ResponseEntity<>("Question NOt Found", HttpStatus.NOT_FOUND);
            }
            logger.info("Added answer successfully");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/question/{question_id}/answer",duration);
            return answerService.addAnswer(question, answer, user);
        }
        catch (Exception e){
            logger.error("Bad request");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request",e);
        }
    }

    @PostMapping(value = "/question")
    public Object addQuestion(@AuthenticationPrincipal User user, @RequestBody Question question) throws Exception {
        try{
            logger.info("Method add question");
            long startTime = System.currentTimeMillis();
            client.incrementCounter("/question");
            if(question.getQuestion_text().isEmpty()){
                logger.error("Question is empty");
                return new ResponseEntity<>("Question is empty", HttpStatus.BAD_REQUEST);
            }
            logger.info("added question successfully");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/question",duration);
            amazonSNSClient.sendEmailToUser(user.getEmail());
            return questionService.addQuestion(question, user);

        }
        catch (Exception e){
            logger.error("Bad Request");
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Found",e);
        }
    }

    @DeleteMapping(value = "/question/{question_id}/answer/{answer_id}")
    public Object deleteAnswerById(@AuthenticationPrincipal User user, @PathVariable String question_id, @PathVariable String answer_id){
        logger.info("Method - Delete answer");
        long startTime = System.currentTimeMillis();
        client.incrementCounter("/question/{question_id}/answer/{answer_id}");
        Object o=answerService.deleteAnswerById(user,question_id,answer_id);
        logger.info("Deleted answer successfully");
        logger.info("added question successfully");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        client.recordExecutionTime("/question/{question_id}/answer/{answer_id}",duration);
        return o;
    }

    @DeleteMapping(value = "/question/{question_id}")
    public Object deleteQuestionById(@AuthenticationPrincipal User user, @PathVariable String question_id){
        try{
            logger.info("Method - delete question");
            long startTime = System.currentTimeMillis();
            client.incrementCounter("/question/{question_id}");
            Object o=questionService.deleteQuestionById(user, question_id);
            logger.error("deleted question successfully");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/question/{question_id}",duration);
            return o;
        }
        catch (Exception e){
            logger.warn("Question Not found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Found",e);
        }
    }

    @PostMapping(value="/question/{question_id}/file",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFile(@AuthenticationPrincipal User loggedUser, @PathVariable String question_id , @RequestParam(value = "file") MultipartFile file){
        logger.info("Method - Upload file for question");
        long startTime = System.currentTimeMillis();
        client.incrementCounter("/question/{question_id}/file");
        Question q = questionService.getQuestionById(question_id);
        if(q==null){
            logger.error("Question not found");
            return new ResponseEntity<>("Id not found",HttpStatus.NOT_FOUND);
        }
        if(!loggedUser.getUserId().equals(q.getUserId())){
            logger.error("specified User cannot update the file");
            return new ResponseEntity<>("User Cannot Update/delete question",HttpStatus.UNAUTHORIZED);
        }
        //String accessKey=env.getProperty("aws-access-key-id");
        //String secretKey=env.getProperty("aws-secret-access-key");
        //logger.info("This is access key message");
        //logger.info(accessKey);
        //BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,secretKey);
        //AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(creds);
        logger.info("Uploading File to s3");
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true).build();
        String bucket_name = "webapp.suheel.vallamkonda";
        UUID uuid = UUID.randomUUID();
        String keyName = question_id+"/"+uuid.toString()+"/"+file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(!extension.equals("png")&&!extension.equals("jpg")&&!extension.equals("jpeg")){
            logger.warn("Invalid image type");
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
            List<QuestionFiles> l = q.getFiles();
                l.add(f);
                q.setFiles(l);
                questionService.updateQuestionById(loggedUser,question_id,q);
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            s3.putObject(bucket_name,keyName,convFile);
            logger.info("Image uploaded successfully");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/question/{question_id}/file",duration);
            return output;

        }catch(AmazonServiceException | IOException e){
            logger.error("AmazonServiceException occurred");
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value="/question/{question_id}/file/{file_id}")
    public Object deleteFile(@AuthenticationPrincipal User loggedUser, @PathVariable String question_id, @PathVariable String file_id ){
        logger.info("Method delete file for question");
        long startTime = System.currentTimeMillis();
        client.incrementCounter("/question/{question_id}/file/{file_id}");
        QuestionFiles files = questionService.getFile(file_id);
        if(files==null)
        {
            logger.error("File not found");
            return new ResponseEntity<>("Id not found",HttpStatus.NOT_FOUND);
        }

        if(!loggedUser.getUserId().equals(files.getUserId()))
        {
            logger.error("Specified user cannot delete the question");
            return new ResponseEntity<>("User Cannot Update/delete question",HttpStatus.UNAUTHORIZED);
        }
        //String accessKey=env.getProperty("aws-access-key-id");
        //String secretKey=env.getProperty("aws-secret-access-key");
        //BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,secretKey);
        //AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(creds);
        logger.info("Deleting file for question from s3 bucket");
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true).build();
        String bucket_name = "webapp.suheel.vallamkonda";
        try {
            s3.deleteObject(bucket_name, files.getS3objectName());
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        if(!files.getUserId().equals(loggedUser.getUserId())){
            logger.error("Specified user cannot delete the file");
            return new ResponseEntity<>("Cannot Delete File",HttpStatus.UNAUTHORIZED);
        }
        try {
            System.out.println(file_id);
            questionService.deleteFile(question_id, file_id);
            Question q2 = questionService.getQuestionById(question_id);
            List l= q2.getFiles();
            l.remove(files);
            q2.setFiles(l);
            questionService.updateQuestionById(loggedUser,question_id,q2);
            logger.info("Deleted file successfully");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/question/{question_id}/file/{file_id}",duration);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Id not found",HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/question/{question_id}/answer/{answer_id}/file",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadAnswerFile(@AuthenticationPrincipal User loggedUser, @PathVariable String question_id ,@PathVariable String answer_id , @RequestParam(value = "file") MultipartFile file){
        logger.info("Method upload answer file");
        long startTime = System.currentTimeMillis();
        client.incrementCounter("/question/{question_id}/answer/{answer_id}/file");
        Answer ans = answerService.getAnswer(answer_id);
        if(ans==null){
            logger.error("answer not found");
            return new ResponseEntity<>("Id Not Found",HttpStatus.NOT_FOUND);
        }
        if(!ans.getUserId().equals(loggedUser.getUserId())){
            logger.error("Specified user cannot upload file");
            return new ResponseEntity<>("Cannot Upload File",HttpStatus.UNAUTHORIZED);
        }

        //String accessKey=env.getProperty("aws-access-key-id");
        //String secretKey=env.getProperty("aws-secret-access-key");
        //BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,secretKey);
        //AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(creds);
        logger.info("Uploading file to the answer");
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true).build();
        String bucket_name = "webapp.suheel.vallamkonda";
        UUID uuid = UUID.randomUUID();
        String keyName = answer_id+"/"+uuid.toString()+"/"+file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if(!extension.equals("png")&&!extension.equals("jpg")&&!extension.equals("jpeg")){
            logger.warn("Invalid type image");
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
            List<AnswerFiles> l = ans.getFiles();
            l.add(f);
            ans.setFiles(l);
            answerService.updateAnswerById(loggedUser,answer_id,ans);
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            s3.putObject(bucket_name,keyName,convFile);
            logger.info("Uploaded file to answer successfully");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/question/{question_id}/answer/{answer_id}/file",duration);
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
        logger.info("Method delete file for answer");
        AnswerFiles files = questionService.getAnswerFile(file_id);
        long startTime = System.currentTimeMillis();
        client.incrementCounter("/question/{question_id}/answer/{answer_id}/file/{file_id}");
        if(files==null)
        {
            logger.info("File not found");
            return new ResponseEntity<>("Id Not Found",HttpStatus.NOT_FOUND);
        }

        if(!files.getUserId().equals(loggedUser.getUserId())){
            logger.error("Specified user cannot delete the file");
            return new ResponseEntity<>("Cannot Delete File",HttpStatus.UNAUTHORIZED);
        }
        //String accessKey=env.getProperty("aws-access-key-id");
        //String secretKey=env.getProperty("aws-secret-access-key");
        //BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,secretKey);
        //AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(creds);
        logger.info("Deleting file for answer from s3 bucket");
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true).build();
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
            a2.setFiles(l);
            answerService.updateAnswerById(loggedUser,answer_id,a2);
            questionService.deleteAnswerFile(answer_id, file_id);
            logger.info("Deleted answer file from s3 successfully");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            client.recordExecutionTime("/question/{question_id}/answer/{answer_id}/file/{file_id}",duration);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Id not found",HttpStatus.NOT_FOUND);
        }
    }
}
