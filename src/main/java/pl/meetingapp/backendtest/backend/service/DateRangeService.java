package pl.meetingapp.backendtest.backend.service;

import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.DateRangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DateRangeService {

    @Autowired
    private DateRangeRepository dateRangeRepository;

    // Metoda do zapisywanai przedzialu daty
    public List<DateRange> saveDateRanges(List<DateRange> dateRanges) {
        return dateRangeRepository.saveAll(dateRanges);
    }

    public List<DateRange> findByMeetingId(Long meetingId) {
        return dateRangeRepository.findByMeetingId(meetingId);
    }

    // Metoda do usuwania przedzialu daty w common dates
    public void deleteById(Long id) {
        dateRangeRepository.deleteById(id);
    }

    // Metoda do pobierania wspólnych dat
    /*
    DZIALNIE:
    - dateRangeRepository.findByMeetingId(meetingId) pobiera wszystkie przedziały dat dla danego spotkania
    - tworzymy lub aktualizujemy mapę userAvailableDates
    - dla każdego użytkownika i każdego przedziału dat dodajemy odpowiednie daty do zbioru
    - zakładamy, że wszystkie daty dostępne dla pierwszego użytkownika są wspólne, kopiując je do commonDates
    - przechodzimy przez wszystkie zbiory dostępnych dat dla pozostałych użytkowników i używamy retainAll, aby pozostawić w commonDates tylko te daty, które są wspólne dla wszystkich użytkowników
     */
    public List<LocalDate> getCommonDatesForMeeting(Long meetingId) {
        List<DateRange> dateRanges = dateRangeRepository.findByMeetingId(meetingId);

        if (dateRanges.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Set<LocalDate>> userAvailableDates = new HashMap<>();

        for (DateRange dateRange : dateRanges) {
            LocalDate startDate = dateRange.getStartDate();
            LocalDate endDate = dateRange.getEndDate();

            Long userId = dateRange.getUser().getId();
            userAvailableDates.putIfAbsent(userId, new HashSet<>());

            Set<LocalDate> availableDates = userAvailableDates.get(userId);
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                availableDates.add(date);
            }
        }

        Set<LocalDate> commonDates = new HashSet<>(userAvailableDates.values().iterator().next());

        for (Set<LocalDate> availableDates : userAvailableDates.values()) {
            commonDates.retainAll(availableDates); // wybiera wszystkie wspolne daty ktore sa zarowno w commonDates i availableDates
        }

        return new ArrayList<>(commonDates);
    }


    // Metoda do znalezienia dat dla danego uzytkownika w danym spotkaniu
    public List<DateRange> findByUserAndMeeting(User user, Long meetingId) {
        return dateRangeRepository.findByUserAndMeetingId(user, meetingId);
    }

    // Metoda do usuwania dat spotkania
    public void deleteAll(List<DateRange> dateRanges) {
        dateRangeRepository.deleteAll(dateRanges);
    }
}