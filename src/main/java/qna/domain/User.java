package qna.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qna.exceptions.UnAuthorizedException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseCreatedAndUpdatedAt {
    public static final GuestUser GUEST_USER = new GuestUser();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String email;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 20, nullable = false)
    private String password;
    @Column(length = 20, nullable = false)
    private String userId;
    @OneToMany(targetEntity = Answer.class, mappedBy = "writer", fetch = FetchType.LAZY)
    private final List<Answer> answers = new ArrayList<>();
    @OneToMany(targetEntity = Question.class, mappedBy = "writer", fetch = FetchType.LAZY)
    private final List<Question> questions = new ArrayList<>();
    @OneToMany(targetEntity = DeleteHistory.class, mappedBy = "deletedBy", fetch = FetchType.LAZY)
    private final List<DeleteHistory> deleteHistories = new ArrayList<>();

    public User(String userId, String password, String name, String email) {
        this(null, userId, password, name, email);
    }

    public User(Long id, String userId, String password, String name, String email) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public void update(User loginUser, User target) {
        if (!matchUserId(loginUser.userId)) {
            throw new UnAuthorizedException();
        }

        if (!matchPassword(target.password)) {
            throw new UnAuthorizedException();
        }

        this.name = target.name;
        this.email = target.email;
    }

    private boolean matchUserId(String userId) {
        return this.userId.equals(userId);
    }

    public boolean matchPassword(String targetPassword) {
        return this.password.equals(targetPassword);
    }

    public boolean equalsNameAndEmail(User target) {
        if (Objects.isNull(target)) {
            return false;
        }

        return name.equals(target.name) &&
                email.equals(target.email);
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void addDeleteHistory(DeleteHistory deleteHistory) {
        deleteHistories.add(deleteHistory);
    }

    public boolean isGuestUser() {
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email=" + email +
                ", name=" + name +
                ", password=" + password +
                ", userId=" + userId +
                ", answers=" + answers +
                ", questions=" + questions +
                ", deleteHistories=" + deleteHistories +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @NoArgsConstructor
    private static class GuestUser extends User {
        public GuestUser(String userId, String password, String name, String email) {
            super(userId, password, name, email);
        }

        public GuestUser(Long id, String userId, String password, String name, String email) {
            super(id, userId, password, name, email);
        }

        @Override
        public boolean isGuestUser() {
            return true;
        }
    }
}
