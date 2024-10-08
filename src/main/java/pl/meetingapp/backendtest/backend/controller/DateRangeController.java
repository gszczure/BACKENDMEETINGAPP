package pl.meetingapp.backendtest.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.DTO.DateRangeDTO;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.service.DateRangeService;
import pl.meetingapp.backendtest.backend.service.MeetingService;
import pl.meetingapp.backendtest.backend.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/date-ranges")
public class DateRangeController {

    @Autowired
    private DateRangeService dateRangeService;

    @Autowired
    private UserService userService;

    @Autowired
    private MeetingService meetingService;

    //Endpdoint do zapisywania dat w bazie danych
    @PostMapping
    public ResponseEntity<List<DateRange>> createDateRanges(@RequestBody List<DateRangeDTO> dateRangesDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        List<DateRange> dateRanges = dateRangesDto.stream().map(dto -> {
            DateRange dateRange = new DateRange();
            Meeting meeting = meetingService.findById(dto.getMeetingId());
            dateRange.setMeeting(meeting);
            dateRange.setUser(user);
            dateRange.setStartDate(dto.getStartDate());
            dateRange.setEndDate(dto.getEndDate());
            return dateRange;
        }).collect(Collectors.toList());

        List<DateRange> savedDateRanges = dateRangeService.saveDateRanges(dateRanges);
        return ResponseEntity.ok(savedDateRanges);
    }

    //Endponit do pobierania dat dla danego spotkania
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<List<DateRange>> getDateRangesForMeeting(@PathVariable Long meetingId) {
        List<DateRange> dateRanges = dateRangeService.findByMeetingId(meetingId);
        return ResponseEntity.ok(dateRanges);
    }

    //Endpoint do usuwania wybranych przedzialow dat (musi miec id przedzialu daty by je usunac)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDateRange(@PathVariable Long id) {
        dateRangeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //Endpoint do pobierania wspolnych dat
    @GetMapping("/meeting/{meetingId}/common-dates")
    public ResponseEntity<List<LocalDate>> getCommonDatesForMeeting(@PathVariable Long meetingId) {
        List<LocalDate> commonDates = dateRangeService.getCommonDatesForMeeting(meetingId);
        return ResponseEntity.ok(commonDates);
    }

}
