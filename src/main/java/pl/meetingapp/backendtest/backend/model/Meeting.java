package pl.meetingapp.backendtest.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "meeting")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 35)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(unique = true)
    private String code;

    @ManyToMany
    @JoinTable(
            name = "meeting_participants",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();

    @Column(name = "meeting_date")
    private String meetingDate;

    @Column(name = "comment")
    private String comment;

    public Meeting(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.code = generateUniqueCode();
    }

    public void addParticipant(User user) {
        this.participants.add(user);
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 5);
    }

}
