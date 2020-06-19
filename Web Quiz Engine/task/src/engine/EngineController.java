package engine;

import engine.models.*;
import engine.models.DataPagination;
import engine.repositories.CompletionRepository;
import engine.repositories.QuizRepository;
import engine.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@RestController
public class EngineController {

    @Autowired
    protected QuizRepository quizRepo;
    @Autowired
    protected UserRepository userRepo;
    @Autowired
    protected CompletionRepository completionRepo;


    @GetMapping("/api/quizzes")
    public ResponseEntity<DataPagination<Quiz>> getQuizPagination(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy));
        Page<Quiz> pagedResult = quizRepo.findAll(paging);
        DataPagination<Quiz> quizPagination = new DataPagination<Quiz>()
                .getPagination(pagedResult);
        return new ResponseEntity<>(quizPagination, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/api/quizzes/completed")
    public ResponseEntity<DataPagination<Completion>> getCompletionPagination(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "completedAt") String sortBy,
            HttpServletRequest request) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        Page<Completion> pagedResult = completionRepo.findByUserId(paging, getUserId(request));
        DataPagination<Completion> completionPagination = new DataPagination<Completion>()
                .getPagination(pagedResult);
        return new ResponseEntity<>(completionPagination, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/api/quizzes/{id}")
    public Optional<Quiz> getQuizById(@PathVariable Long id) {
        Optional<Quiz> quiz = quizRepo.findById(id);
        if (quiz.equals(Optional.empty())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id not found.");
        }
        return quiz;
    }

    @PostMapping(value = "/api/quizzes", consumes = "application/json")
    public ResponseEntity<Quiz> addQuiz(@Valid @RequestBody Quiz quiz, HttpServletRequest request,
                                        BindingResult bindingResult) {
        HttpStatus status;
        Quiz newQuiz = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            status = HttpStatus.BAD_REQUEST;
        } else {
            quiz.setUserId(getUserId(request));
            newQuiz = quizRepo.save(quiz);
            status = HttpStatus.OK;
        }
        return new ResponseEntity<>(newQuiz, headers, status);
    }

    @PostMapping(value = "/api/quizzes/{id}/solve", consumes = "application/json")
    public Feedback postAnswer(@PathVariable Long id, @RequestBody Answer answer, HttpServletRequest request) {
        Integer[] correctAnswer = quizRepo.findById(id).orElse(new Quiz()).getAnswer();
        if (Arrays.equals(answer.getAnswer(), correctAnswer)) {
            Completion completion = new Completion();
            completion.setId(id);
            completion.setUserId(getUserId(request));
            completion.setCompletedAt(new Timestamp(new Date().getTime()));
            completionRepo.save(completion);
            return new Feedback(true);
        }
        return new Feedback(false);
    }

    @PostMapping(value = "/api/register", consumes = "application/json")
    public ResponseEntity<String> register(@Valid @RequestBody User user, BindingResult bindingResult) {
        HttpStatus status;
        String message;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (bindingResult.hasErrors()) {
            status = HttpStatus.BAD_REQUEST;
            message = "Invalid email or password.";
        } else if (!userRepo.findByEmail(user.getEmail()).equals(Optional.empty())) {
            status = HttpStatus.BAD_REQUEST;
            message = "User email already exists.";
        } else {
            // encrypt password
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            userRepo.save(user);
            status = HttpStatus.OK;
            message = "User registered successfully.";
        }
        return new ResponseEntity<>(message, headers, status);
    }

    @DeleteMapping(value = "/api/quizzes/{id}")
    public ResponseEntity<Quiz> deleteQuiz(@PathVariable Long id, HttpServletRequest request) {
        Optional<Quiz> quiz = quizRepo.findById(id);
        if (quiz.isPresent()) {
            if (getUserId(request).equals(quiz.get().getUserId())) {
                quizRepo.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.notFound().build();
    }

    private Long getUserId(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        return userRepo.findByEmail(principal.getName()).get().getId();
    }
}
