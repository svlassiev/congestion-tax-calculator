package congestion.calculator;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class CongestionTaxCalculator {

    private static ZoneId UTC = ZoneId.of("UTC");
    private static ZoneId CET = ZoneId.of("CET");
    private static final List<DayOfWeek> WEEKEND = Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static final int MAX_FEE = 60;
    private static final int MAX_WINDOW_IN_MINUTES = 60;


    public static void main(String[] args) {
        System.out.println("Congestion Tax Calculator");
    }

    public int getTax(TaxableVehicle vehicle, LocalDateTime[] dateTimesUTC)
    {
        if (null == dateTimesUTC || dateTimesUTC.length == 0) {
            return 0;
        }

        // Adjust time to Gothenburg timezone (CET) and sort dates
        List<ZonedDateTime> sortedZonedTime = Arrays.stream(dateTimesUTC)
                .map(dateTimeUTC -> ZonedDateTime.of(dateTimeUTC, UTC).withZoneSameInstant(CET))
                .sorted()
                .collect(Collectors.toUnmodifiableList());

        // Group toll passes by day
        // Assume all the passes have happened during the same year
        Collection<List<ZonedDateTime>> tollPassesByDay = sortedZonedTime.stream().collect(Collectors.groupingBy(zonedDateTime -> zonedDateTime.getDayOfYear())).values();

        int totalFee = 0;

        for (List<ZonedDateTime> tollPasses : tollPassesByDay) {
            int dayFee = 0;

            int passNumber = 0;
            while (passNumber < tollPasses.size()) {
                ZonedDateTime intervalStartTime = tollPasses.get(passNumber);
                int intervalFee = 0;
                while (passNumber < tollPasses.size() && Duration.between(intervalStartTime, tollPasses.get(passNumber)).toMinutes() <= MAX_WINDOW_IN_MINUTES) {
                    int passFee = getTollFee(tollPasses.get(passNumber), vehicle);
                    if (passFee > intervalFee) intervalFee = passFee;
                    passNumber++;
                }
                dayFee += intervalFee;
            }

            if (dayFee > MAX_FEE) dayFee = MAX_FEE;
            totalFee += dayFee;
        }
      
        return totalFee;
    }

    int getTollFee(ZonedDateTime zonedDateTime, TaxableVehicle vehicle)
    {
        if (vehicle.isTollFree() || isTollFreeDate(zonedDateTime)) return 0;

        int hour = zonedDateTime.getHour();
        int minute = zonedDateTime.getMinute();

        if (hour == 6 && minute <= 29) return 8;
        else if (hour == 6) return 13;
        else if (hour == 7) return 18;
        else if (hour == 8 && minute <= 29) return 13;
        else if (hour >= 8 && hour <= 14) return 8;
        else if (hour == 15 && minute <= 29) return 13;
        else if (hour == 15 || hour == 16) return 18;
        else if (hour == 17) return 13;
        else if (hour == 18 && minute <= 29) return 8;
        else return 0;
    }

    // isToolFreeDate expects date localized to Gothenburg (CET timezone)
    private Boolean isTollFreeDate(ZonedDateTime zonedDate)
    {
        // No charging on weekends
        if (WEEKEND.contains(zonedDate.getDayOfWeek()))
        {
            return true;
        }

        // No charging in July
        if (Month.JULY.equals(zonedDate.getMonth()))
        {
            return true;
        }

        // No charging during public holidays and days before public holidays
        Set<Integer> publicHolidays = getPublicHolidays(zonedDate.getYear());
        if (publicHolidays.contains(zonedDate.getDayOfYear()) || publicHolidays.contains(zonedDate.getDayOfYear() + 1))
        {
            return true;
        }

        // Manually check 31st of December as soon as it's the day before 1st of January of the next year
        if (zonedDate.getDayOfYear() == 31 && zonedDate.getMonth().equals(Month.DECEMBER))
        {
            return true;
        }

        return false;
    }

    // getPublicHolidays is only limited to 2013 and fixed holidays for other years
    private Set<Integer> getPublicHolidays(int year)
    {
        Set<Integer> publicHolidays = new HashSet<>();
        switch (year) {
            case(2013):
                publicHolidays.addAll(Arrays.asList(
                        LocalDate.of(year, Month.MARCH, 29).getDayOfYear(),
                        LocalDate.of(year, Month.MARCH, 31).getDayOfYear(),
                        LocalDate.of(year, Month.APRIL, 1).getDayOfYear(),
                        LocalDate.of(year, Month.MAY, 9).getDayOfYear(),
                        LocalDate.of(year, Month.MAY, 19).getDayOfYear(),
                        LocalDate.of(year, Month.JUNE, 22).getDayOfYear(),
                        LocalDate.of(year, Month.NOVEMBER, 2).getDayOfYear()));
            default:
                publicHolidays.addAll(Arrays.asList(
                        LocalDate.of(year, Month.JANUARY, 1).getDayOfYear(),
                        LocalDate.of(year, Month.JANUARY, 6).getDayOfYear(),
                        LocalDate.of(year, Month.MAY, 1).getDayOfYear(),
                        LocalDate.of(year, Month.JUNE, 6).getDayOfYear(),
                        LocalDate.of(year, Month.DECEMBER, 25).getDayOfYear(),
                        LocalDate.of(year, Month.DECEMBER, 26).getDayOfYear()));
        }
        return publicHolidays;
    }
}
