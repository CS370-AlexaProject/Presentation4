package com.neong.voice.example;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/********************************************
 Cathrina Lloyd
 PROJECT: com.neong.voice.example.MoonlightAPI
 CLASS  : com.neong.voice.example.MoonlightAPI
 ********************************************/
public class MoonlightAPI {
    // Month variables
    static final String JAN = "January";
    static final String FEB = "February";
    static final String MAR = "March";
    static final String APR = "April";
    static final String MAY = "May";
    static final String JUN = "June";
    static final String JUL = "July";
    static final String AUG = "August";
    static final String SEP = "September";
    static final String OCT = "October";
    static final String NOV = "November";
    static final String DEC = "December";

    // Date variables
    static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX";
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
    static final String ALEXA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    static final SimpleDateFormat ALEXA_DATE_FORMAT = new SimpleDateFormat(ALEXA_DATE_PATTERN);

    public MoonlightAPI(){}

    public ArrayList<String> getEvents(String alexaDate){
        ArrayList<String> events = new ArrayList<>();
        try {
            ArrayList<JSONObject> jsonEvents = sortedJSONEvents(getFromAPIByAlexaDate(alexaDate));
            for(JSONObject event : jsonEvents)
                events.add(JSONEventToAlexaLiteral(event));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    public ArrayList<JSONObject> getJSONEvents(String alexaDate){
        ArrayList<JSONObject> events = new ArrayList<>();
        try {
            events = getFromAPIByAlexaDate(alexaDate);
            events = sortedJSONEvents(events);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    private ArrayList<JSONObject> sortedJSONEvents(ArrayList<JSONObject> events) throws JSONException, ParseException {
        ArrayList<JSONObject> sortedEvents = new ArrayList<>();
        Date today = Calendar.getInstance().getTime();
        for(JSONObject event : events){
            Date eventDate = ALEXA_DATE_FORMAT.parse(event.getString("start_date"));
            //System.out.println(eventDate.compareTo(today));
            if(eventDate.compareTo(today) > 0){
                sortedEvents.add(event);
            }
        }
        Collections.reverse(sortedEvents);
        return sortedEvents;
    }

    private String JSONEventToAlexaLiteral(JSONObject event) throws JSONException, ParseException {
        /*return "On" + JSONDateToAlexaLiteral(event.getString("start_date")) + ", "  // date of event
                + "there is a " + event.getString("title") + " event "  // name of event
                + JSONTimeToAlexaLiteral(event.getString("start_date"),event.getString("end_date"));  // time of event*/
        return event.getString("title") + // name of event
                ", " + JSONTimeToAlexaLiteral(event.getString("start_date"),event.getString("end_date")) +
                " on" + JSONDateToAlexaLiteral(event.getString("start_date"));

    }

    private ArrayList<JSONObject> getFromAPIByAlexaDate(String alexaDate) throws Exception {
        ArrayList<Date> dates = parseAlexaDate(alexaDate);
        ArrayList<JSONObject> events = getFromAPIByDate(dates.get(0),dates.get(1));
        return events;
    }

    private String JSONTimeToAlexaLiteral(String inputStartTime, String inputEndTime){
        String startHour = inputStartTime.substring(11,13);
        String startMinute = inputStartTime.substring(14,16);
        String endHour = inputEndTime.substring(11,13);
        String endMinute = inputEndTime.substring(14,16);


        if(startHour == "00" && startMinute == "00" && endHour == "00" && endMinute == "00")
            return "";

        // Converts the 24 hour time to 12 hour times
        int startHourInt = Integer.parseInt(startHour);
        if(startHourInt > 12) {
            startHourInt -= 12;
        }
        startHour = String.valueOf(startHourInt);

        int endHourInt = Integer.parseInt(endHour);
        if(endHourInt > 12) {
            endHourInt -= 12;
        }
        endHour = String.valueOf(endHourInt);

        if(startHourInt == 0)
            startHour = "12";
        if(endHourInt == 0)
            endHour = "12";

        String res = "at " + startHour + ":" + startMinute + " to " + endHour + ":" + endMinute;
        return res;

    }

    private String JSONDateToAlexaLiteral(String inputDate) throws ParseException {
        int year = Integer.parseInt(inputDate.substring(0,4)),
                month = Integer.parseInt(inputDate.substring(5,7)),
                day = Integer.parseInt(inputDate.substring(8,10));
        String yearString = String.valueOf(year),
                monthString = "",
                dayString = String.valueOf(day),
                res;

        Date today = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int thisYear = cal.get(Calendar.YEAR);

        switch (month) {
            case 1:
                monthString = "January";
                break;
            case 2:
                monthString = "February";
                break;
            case 3:
                monthString = "March";
                break;
            case 4:
                monthString = "April";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "June";
                break;
            case 7:
                monthString = "July";
                break;
            case 8:
                monthString = "August";
                break;
            case 9:
                monthString = "September";
                break;
            case 10:
                monthString = "October";
                break;
            case 11:
                monthString = "November";
                break;
            case 12:
                monthString = "December";
                break;
        }

        //return " " + inputDate.substring(0,10) + " ";
        res = " " + monthString + " " + dayString;
        if(thisYear < year)
            res += ", " + yearString;
        return res;
    }

    private ArrayList<Date> parseAlexaDate(String alexaDate) throws ParseException {
        ArrayList<Date> results = new ArrayList<>();
        if(alexaDate.length() == 10){
            results.add(ALEXA_DATE_FORMAT.parse(alexaDate + "T00:00:00"));
            results.add(ALEXA_DATE_FORMAT.parse(alexaDate + "T23:59:59"));
        }
        else if(alexaDate.length() == 4){
            if(alexaDate.contains("X")){
                String decadeDate = alexaDate.substring(0,3) + "0-01-01";
                results.add(ALEXA_DATE_FORMAT.parse(decadeDate + "T00:00:00"));
                decadeDate = alexaDate.substring(0,3) + "9-12-31";
                results.add(ALEXA_DATE_FORMAT.parse(decadeDate + "T23:59:59"));
            }
            else{
                String yearDate = alexaDate + "-01-01";
                results.add(ALEXA_DATE_FORMAT.parse(yearDate + "T00:00:00"));
                yearDate = alexaDate + "-12-31";
                results.add(ALEXA_DATE_FORMAT.parse(yearDate + "T23:59:59"));
            }
        }
        else if(alexaDate.contains("WI")){
            String winterDate = alexaDate.substring(0,4) + "-12-01";
            results.add(ALEXA_DATE_FORMAT.parse(winterDate + "T00:00:00"));
            int winterYear = Integer.parseInt(winterDate.substring(0,4));
            winterYear++;
            winterDate = String.valueOf(winterYear) + "-02-28";
            results.add(ALEXA_DATE_FORMAT.parse(winterDate + "T23:59:59"));
        }
        else if(alexaDate.contains("SP")){
            String springDate = alexaDate.substring(0,4) + "-03-01";
            results.add(ALEXA_DATE_FORMAT.parse(springDate + "T00:00:00"));
            springDate = springDate.substring(0,4) + "-05-31";
            results.add(ALEXA_DATE_FORMAT.parse(springDate + "T23:59:59"));
        }
        else if(alexaDate.contains("SU")){
            String summerDate = alexaDate.substring(0,4) + "-06-01";
            results.add(ALEXA_DATE_FORMAT.parse(summerDate + "T00:00:00"));
            summerDate = summerDate.substring(0,4) + "-08-31";
            results.add(ALEXA_DATE_FORMAT.parse(summerDate + "T23:59:59"));
        }
        else if(alexaDate.contains("FA")){
            String fallDate = alexaDate.substring(0,4) + "-09-01";
            results.add(ALEXA_DATE_FORMAT.parse(fallDate + "T00:00:00"));
            fallDate = fallDate.substring(0,4) + "-10-30";
            results.add(ALEXA_DATE_FORMAT.parse(fallDate + "T23:59:59"));
        }
        else if(alexaDate.contains("WE")){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(alexaDate.substring(6,8)) + 1);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            results.add(ALEXA_DATE_FORMAT.parse(ALEXA_DATE_FORMAT.format(cal.getTime()) + "T00:00:00"));
            cal.add(Calendar.DATE, 1);
            results.add(ALEXA_DATE_FORMAT.parse(ALEXA_DATE_FORMAT.format(cal.getTime()) + "T00:00:00"));
        }
        else if(alexaDate.contains("W") && alexaDate.length() == 8){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(alexaDate.substring(6,8)) + 1);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            results.add(ALEXA_DATE_FORMAT.parse(ALEXA_DATE_FORMAT.format(cal.getTime())));
            cal.add(Calendar.DATE, 7);
            results.add(ALEXA_DATE_FORMAT.parse(ALEXA_DATE_FORMAT.format(cal.getTime())));
        }
        else if(alexaDate.length() == 7){
            String monthYear = alexaDate.substring(0,4);
            String monthMonth = alexaDate.substring(5,7);

            results.add(ALEXA_DATE_FORMAT.parse(monthYear + "-" + monthMonth + "-01" + "T00:00:00"));

            int monthYearNum = Integer.parseInt(monthYear);
            int monthMonthNum = Integer.parseInt(monthMonth);

            monthMonthNum++;
            if(monthMonthNum == 13){
                monthYearNum++;
                monthMonthNum = 1;
            }

            monthYear = String.valueOf(monthYearNum);
            monthMonth = String.valueOf(monthMonthNum);

            if(monthMonth.length() == 1)
                monthMonth = "0" + monthMonth;

            results.add(ALEXA_DATE_FORMAT.parse(monthYear + "-" + monthMonth + "-01" + "T00:00:00"));
        }

        return results;
    }

    private ArrayList<JSONObject> getFromAPIByDate(Date startDate, Date endDate) throws Exception {
        ArrayList<JSONObject> results = new ArrayList<>();
        ArrayList<JSONObject> mappedAPIResults = getFromAPI();
        Date JSONDate;
        for(JSONObject buff : mappedAPIResults){
            JSONDate = DATE_FORMAT.parse(buff.getString("start_date"));
            if((JSONDate.after(startDate) && JSONDate.before(endDate))
                    || JSONDate.equals(startDate)
                    || JSONDate.equals(endDate)){
                results.add(buff);
            }
        }
        return results;
    }

    private ArrayList<JSONObject> getFromAPI() throws Exception {
        ArrayList<JSONObject> mappedAPIResults = new ArrayList<>();
        URL api = new URL("http://moonlight.cs.sonoma.edu/api/v1/events/event/?format=json");
        URLConnection apiConnection = api.openConnection();
        apiConnection.connect();
        BufferedReader in = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
        String inputLine, rawJSON = "";
        while ((inputLine = in.readLine()) != null)
            rawJSON += inputLine;
        in.close();

        // Clean the JSON string
        rawJSON = rawJSON.substring(1, rawJSON.length() - 1);  // remove [ and ]
        String[] cleanedJSON = rawJSON.split(",(?=\\{)");  // convert rawJSON into an array of objects

        for (String buff : cleanedJSON)  // for each element of the cleanedJSON array
            mappedAPIResults.add(new JSONObject(buff));  // convert the String into a new object in the array

        return mappedAPIResults;
    }
}
