package com.neong.voice.example;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.neong.voice.model.base.Conversation;

import java.util.ArrayList;
import java.util.Map;

/**
 * This is an implementation of a Conversation subclass. It is important to
 * register your intents by adding them to the supportedIntentNames array in
 * the constructor. Your conversation must internally track the current state
 * of the conversation and all state transitions.
 *
 * @author Nazania Barraza
 */

public class SSUCalendar extends Conversation {
    //Intent names
    private final static String INTENT_SSUCALENDAR = "SSUCalendarIntent"; // General Calendar
    private final static String INTENT_SSUACADEMICC = "SSUAcademicCalendarIntent"; // Going straight to Academic Calendar
    private final static String INTENT_SSUSPORTSC = "SSUSportingEventsCalendarIntent"; // Going straight to Sporting Events Calendar
    private final static String INTENT_WHICHCALENDARA = "WhichCalendarA"; // Academic
    private final static String INTENT_WHICHCALENDARS = "WhichCalendarS"; // Sport
    private final static String INTENT_ACADEMICTYPEH = "holidays";
    private final static String INTENT_ACADEMICTYPEAD = "academicdates";
    private final static String INTENT_ACADEMICTYPEFD = "financialdeadlines";
    private final static String INTENT_SPORTTYPES = "soccer";
    private final static String INTENT_SPORTTYPEV = "volleyball";
    private final static String INTENT_SPORTTYPECC = "crosscountry";
    private final static String INTENT_SPORTTYPEG = "golf";
    private final static String INTENT_SPORTTYPET = "tennis";
    private final static String INTENT_SPORTTYPEB = "basketball";
    private final static String INTENT_SPORTTYPEBB = "baseball";
    private final static String INTENT_SPORTTYPETF = "trackandfield";
    private final static String INTENT_SPORTTYPEWP = "waterpolo";
    private final static String INTENT_SPORTTYPESB = "softball";
    //Slots
    private final static String SLOT_RELATIVE_TIME = "timeOfDay";
    //State
    private final static Integer STATE_WAITING_WHATCALENDAR = 100000; // After MySSUCalendar-> list 3 events-> Ask if wanted info from specific calendar
    private final static Integer STATE_WAITING_CONTINUE = 100001; // In any calendar-> Alexa keep reading events.
    private final static Integer STATE_WAITING_WHATSUBCALENDAR = 100002;
    private final static Integer STATE_HAS_EVENTS = 100003;
    private final static Integer STATE_NO_EVENTS = 100004;
    //Calendars
    private final static Integer GENERAL_CALENDAR = 1;
    private final static Integer ACADEMIC_CALENDAR = 2;
    private final static Integer SPORTS_CALENDAR = 3;
    private final static Integer FAIL_CALENDAR = -1;
    //Static Responses (For Demo)
    private final static String STATIC_AR_1 = "On October 25th 2016, Petition to withdraw from a class with a 20 dollar administration fee continues";
    private final static String STATIC_AR_2 = "On October 26th 2016, Petition to withdraw from a class with a 20 dollar administration fee continues";
    private final static String STATIC_AR_3 = "On October 27th 2016, Petition to withdraw from a class with a 20 dollar administration fee continues";
    private final static String STATIC_SR_1 = "Women's golf tournament on October 25th 2016, at C S U San Marcos";
    private final static String STATIC_SR_2 = "Soccer game on October 27th 2016, against Cal State Dominguez Hills, at Sonoma State. Women at 12:30 P M and Men at 3 P M";
    private final static String STATIC_SR_3 = "Men's tennis match on October 28th 2016, at Saint Mary's Invitational";
    //Session state storage key
    private final static String SESSION_SSUCALENDAR_STATE = "GeneralCalendarState";
    private final static String SESSION_ACADEMICCALENDAR_STATE = "AcademicCalendarState";
    private final static String SESSION_SPORTCALENDAR_STATE = "SportsCalendarState";
    private final static String SESSION_ACADEMICTYPE_STATE = "AcademicType";
    private final static String SESSION_SPORTTYPE_STATE = "SportType";
    private final static String SESSION_LISTEVENTS_STATE = "ListEvents";
    //Globals
    private static MoonlightAPI api = new MoonlightAPI();
    private static ArrayList<String> events = null;

    //Response strings
    private final static String RESPONSE_NOMOREEVENTS = "Okay. What date?";
    private final static String RESPONSE_BADEND = "Did you even listen to my question?";
    private final static String RESPONSE_GOODEND = "Have a nice day!";

    public SSUCalendar() {
        super();

        //Add custom intent names for dispatcher use
        supportedIntentNames.add(INTENT_SSUCALENDAR);
        supportedIntentNames.add(INTENT_SSUACADEMICC);
        supportedIntentNames.add(INTENT_SSUSPORTSC);
        supportedIntentNames.add(INTENT_WHICHCALENDARA);
        supportedIntentNames.add(INTENT_WHICHCALENDARS);
        supportedIntentNames.add("AMAZON.YesIntent");
        supportedIntentNames.add("AMAZON.NoIntent");
        supportedIntentNames.add(INTENT_ACADEMICTYPEH);
        supportedIntentNames.add(INTENT_ACADEMICTYPEAD);
        supportedIntentNames.add(INTENT_ACADEMICTYPEFD);
        supportedIntentNames.add(INTENT_SPORTTYPES);
        supportedIntentNames.add(INTENT_SPORTTYPEV);
        supportedIntentNames.add(INTENT_SPORTTYPECC);
        supportedIntentNames.add(INTENT_SPORTTYPEG);
        supportedIntentNames.add(INTENT_SPORTTYPET);
        supportedIntentNames.add(INTENT_SPORTTYPEB);
        supportedIntentNames.add(INTENT_SPORTTYPEBB);
        supportedIntentNames.add(INTENT_SPORTTYPETF);
        supportedIntentNames.add(INTENT_SPORTTYPEWP);
        supportedIntentNames.add(INTENT_SPORTTYPESB);
    }


    @Override
    public SpeechletResponse respondToIntentRequest(IntentRequest intentReq, Session session) {
        Intent intent = intentReq.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        Map<String, Slot> slots = intent.getSlots();  // get slots

        SpeechletResponse response = null;

        // Initial User Question
        if (INTENT_SSUCALENDAR.equals(intentName)) { //#1 //Works
            Slot dateSlot = slots.get("Date");
            String dateRequest = dateSlot.getValue();
            setEvents(session, dateRequest);
            response = listThreeEventsAsIntent(session);
        }


        // Continue
        else if ("AMAZON.YesIntent".equals(intentName)) {
            // listevents has events in it
            if(SESSION_LISTEVENTS_STATE != null &&
                    STATE_HAS_EVENTS.compareTo((Integer)session.getAttribute(SESSION_LISTEVENTS_STATE)) == 0)
                response = listThreeEventsAsIntent(session);
            else if(SESSION_LISTEVENTS_STATE != null &&
                    STATE_NO_EVENTS.compareTo((Integer)session.getAttribute(SESSION_LISTEVENTS_STATE)) == 0) {
                response = newAskResponse(RESPONSE_NOMOREEVENTS, false, RESPONSE_NOMOREEVENTS, false);
            }
            else {
                response = newTellResponse(RESPONSE_BADEND, true);
            }
        } else if ("AMAZON.NoIntent".equals(intentName)) {
            if(session.getAttribute(SESSION_LISTEVENTS_STATE) != null)
                response = newTellResponse(RESPONSE_GOODEND, true);
            else {
                response = newTellResponse(RESPONSE_BADEND, false);
            }
        } else{
            response = newTellResponse(RESPONSE_BADEND, false);
        }
        return response;
    }

    private void setEvents(Session session, String dateRequest){
        events = api.getEvents(dateRequest);
        session.setAttribute(SESSION_LISTEVENTS_STATE, STATE_HAS_EVENTS);
    }

    private SpeechletResponse listThreeEventsAsIntent(Session session){
        String responseString = "";
        int numEventsRetrieved;

        if(events.size() >= 3)
            numEventsRetrieved = 3;
        else
            numEventsRetrieved = events.size();

        for(int i = 0; i < numEventsRetrieved; i++){
            responseString += events.get(0) + ". ";
            events.remove(0);
        }

        if(events.size() == 0) {
            session.setAttribute(SESSION_LISTEVENTS_STATE, STATE_NO_EVENTS);
            return newAskResponse(responseString + " Those are all the events I have information on. Would you like to hear about another date?", false, "Would you like to hear about another date?", false);
        }
        else
            return newAskResponse(responseString + "Would you like me to continue?", false, "Would you like me to continue?", false);
    }


    private SpeechletResponse listThreeEventsASIntent(int calendar_type, IntentRequest intentreq, Session session) {
        SpeechletResponse response = null;
        if (session.getAttribute(SESSION_ACADEMICTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer) session.getAttribute(SESSION_ACADEMICTYPE_STATE)) == 0) {
            response = newAskResponse(STATIC_AR_1 + "," + STATIC_AR_2 + "," + STATIC_AR_3 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
            session.removeAttribute(SESSION_ACADEMICTYPE_STATE);
            session.setAttribute(SESSION_ACADEMICCALENDAR_STATE, STATE_WAITING_CONTINUE);
        } else if (session.getAttribute(SESSION_SPORTTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer) session.getAttribute(SESSION_SPORTTYPE_STATE)) == 0) {
            response = newAskResponse(STATIC_SR_1 + "," + STATIC_SR_2 + "," + STATIC_SR_3 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
            session.removeAttribute(SESSION_SPORTTYPE_STATE);
            session.setAttribute(SESSION_SPORTCALENDAR_STATE, STATE_WAITING_CONTINUE);
        } else if (session.getAttribute(SESSION_ACADEMICCALENDAR_STATE) != null && STATE_WAITING_CONTINUE.compareTo((Integer) session.getAttribute(SESSION_ACADEMICCALENDAR_STATE)) == 0) {
            response = newAskResponse("Academic Calendar events continuing..." + "Would you like to continue?", false, "Would you like to continue?", false);
            session.setAttribute(SESSION_ACADEMICCALENDAR_STATE, STATE_WAITING_CONTINUE);
        } else if (session.getAttribute(SESSION_SPORTCALENDAR_STATE) != null && STATE_WAITING_CONTINUE.compareTo((Integer) session.getAttribute(SESSION_SPORTCALENDAR_STATE)) == 0) {
            response = newAskResponse("Sports Calendar events continuing..." + "Would you like to continue?", false, "Would you like to continue?", false);
            session.setAttribute(SESSION_SPORTCALENDAR_STATE, STATE_WAITING_CONTINUE);
        } else {
            response = newTellResponse("I don't have any information on that.", false);
        }
        return response;
    }


    private SpeechletResponse handleWhatSubcalendarType(IntentRequest intentreq, Session session, int calendar_type) {
        SpeechletResponse response = null;
        if (session.getAttribute(SESSION_SSUCALENDAR_STATE) != null && STATE_WAITING_WHATCALENDAR.compareTo((Integer) session.getAttribute(SESSION_SSUCALENDAR_STATE)) == 0) {
            if (calendar_type == ACADEMIC_CALENDAR) {
                response = newAskResponse("Did you want to hear about a specific category in the Academic Calendar? Tell me what category", false, "Say holiday, academic date, or financial deadline", false);
                session.removeAttribute(SESSION_SSUCALENDAR_STATE);
                session.setAttribute(SESSION_ACADEMICTYPE_STATE, STATE_WAITING_WHATSUBCALENDAR);
            } else if (calendar_type == SPORTS_CALENDAR) {
                response = newAskResponse("Did you want to hear about a specific sport? Tell me what sport", false, "What sport did you want?", false);
                session.removeAttribute(SESSION_SSUCALENDAR_STATE);
                session.setAttribute(SESSION_SPORTTYPE_STATE, STATE_WAITING_WHATSUBCALENDAR);
            }
        } else if (calendar_type == ACADEMIC_CALENDAR) {
            response = newAskResponse("Did you want to hear about a specific category in the Academic Calendar? Tell me what category", false, "Say holiday, academic date, or financial deadline", false);
            //session.removeAttribute(SESSION_SSUCALENDAR_STATE);
            session.setAttribute(SESSION_ACADEMICTYPE_STATE, STATE_WAITING_WHATSUBCALENDAR);
        } else if (calendar_type == SPORTS_CALENDAR) {
            response = newAskResponse("Did you want to hear about a specific sport? Tell me what sport", false, "What sport did you want?", false);
            //session.removeAttribute(SESSION_SSUCALENDAR_STATE);
            session.setAttribute(SESSION_SPORTTYPE_STATE, STATE_WAITING_WHATSUBCALENDAR);
        } else {
            response = newTellResponse("Fail in handle what sub calendar type function", false);
        }
        return response;
    }

    private SpeechletResponse listNextSpecificSubcalendarEvent(IntentRequest intentreq, Session session, String subcalendar_type) {
        SpeechletResponse response = null;
        if (session.getAttribute(SESSION_ACADEMICTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer) session.getAttribute(SESSION_ACADEMICTYPE_STATE)) == 0) {
            if (subcalendar_type == "holiday") {
                response = newTellResponse("The next school holiday is Veteran's Day on November 11th 2016", false);
                session.removeAttribute(SESSION_ACADEMICTYPE_STATE);
            } else if (subcalendar_type == "academicdate") {
                response = newTellResponse("The next academic date is on October 26th 2016, Petition to withdraw from a class with a 20 dollar administration fee continues", false);
                session.removeAttribute(SESSION_ACADEMICTYPE_STATE);
            } else if (subcalendar_type == "financialdeadline") {
                response = newTellResponse("The next financial deadline is on November 1st 2016, the Enrollment Deposit is due for new spring 2017 students", false);
                session.removeAttribute(SESSION_ACADEMICTYPE_STATE);
            }
        } else if (session.getAttribute(SESSION_SPORTTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer) session.getAttribute(SESSION_SPORTTYPE_STATE)) == 0) {
            if (subcalendar_type == "soccer") {
                response = newTellResponse("The next soccer game is on October 27th 2016, against Cal State Dominguez Hills, at Sonoma State. Women at 12:30 P M and Men at 3 P M", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            } else if (subcalendar_type == "volleyball") {
                response = newTellResponse("The next volleyball game is on October 28th 2016, at Stanislaus State, at 7 P M", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            } else if (subcalendar_type == "crosscountry") {
                response = newTellResponse("The next cross country meet is on November 5th 2016, at N C A A West Regional in Billings Montana", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            } else if (subcalendar_type == "golf") {
                response = newTellResponse("The next golf tournament is on February 2nd 2017, at Point Loma Nazarene University", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            } else if (subcalendar_type == "tennis") {
                response = newTellResponse("The next tennis match is Mens's tennis on October 28th 2016, at Saint Mary's Invitational", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            } else if (subcalendar_type == "basketball") {
                response = newTellResponse("The next basketball game is Women's at San Jose State, at 5 P M", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            } else if (subcalendar_type == "baseball") {
                response = newTellResponse("The next baseball game is against San Francisco State, at Sonoma State, at 11 A M", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            } else if (subcalendar_type == "trackandfield") {
                response = newTellResponse("The next track and field meet is on February 27th 2016, at Johnny Mathis Invitational, at 9 A M", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            } else if (subcalendar_type == "waterpolo") {
                response = newTellResponse("The next water polo game is on January 1st 2017, against U C San Diego, at Santa Barbara, at 12 P M", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            } else if (subcalendar_type == "softball") {
                response = newTellResponse("The next softball game is on February 10th 2017, at Cal State Monterey Bay, at 12 P M", false);
                session.removeAttribute(SESSION_SPORTTYPE_STATE);
            }
        } else {
            response = newTellResponse("I don't have any information on that", false);
        }
        return response;
    }
}
