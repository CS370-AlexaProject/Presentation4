package com.neong.voice.example;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.neong.voice.model.base.Conversation;
import java.util.*;

/*
 * This is an implementation of a Conversation subclass. It is important to
 *  register your intents by adding them to the supportedIntentNames array in 
 *  the constructor. Your conversation must internally track the current state 
 *  of the conversation and all state transitions.
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
    
    //Calendars
    private final static Integer GENERAL_CALENDAR = 1;
    private final static Integer ACADEMIC_CALENDAR = 2;
    private final static Integer SPORTS_CALENDAR = 3;
    private final static Integer FAIL_CALENDAR = -1;
    
    int continue_counter = 0; //**will keep track of how many times you have entered the listThreeEventsAS function.
    ////might need to add it to the function parameter.
    //Static Responses (For Demo)
    private final static String STATIC_AR_1 = "On November 14th, registration for spring 2017 begins by appointment";
    private final static String STATIC_AR_2 = "on November 15th, last day to petition to withdraw from a class with a 20 dollar administrative fee";
    private final static String STATIC_AR_3 = "on November 15th, spring 2017 registration by appointment continues";
    private final static String STATIC_AR_4 = "on November 16th, spring 2017 registration by appointment continues";
    private final static String STATIC_AR_5 = "on November 17th, spring 2017 registration by appointment continues";
    private final static String STATIC_AR_6 = "On November 18th, spring 2017 registration by appointment continues";
    private final static String STATIC_AR_7 = "On November 19th, spring 2017 registration continues";
    private final static String STATIC_AR_8 = "on November 20th, spring 2017 registration continues";
    private final static String STATIC_AR_9 = "on November 21st, spring 2017 registration continues";
    private final static String STATIC_AR_10 = "on November 22nd, spring 2017 registration continues";
    private final static String STATIC_AR_11 = "on November 23rd, spring 2016 registration continues";
    private final static String STATIC_AR_12 = "on November 23rd, Thanksgiving break begins";
    private final static String STATIC_AR_13 = "on November 24th, spring 2017 registration continues";
    private final static String STATIC_AR_14 = "on November 24th, Thanksgiving break continues";
    private final static String STATIC_AR_15 = "on November 25th, spring 2017 registration continues";
    private final static String STATIC_AR_16 = "on November 25th, Last day of Thanksgiving break";
    private final static String STATIC_AR_17 = "on November 26th, spring 2017 registration continues";
    private final static String STATIC_AR_18 = "on November 27th, spring 2017 registration continues";
    private final static String STATIC_AR_19 = "on November 28th, spring 2017 registration continues";
    private final static String STATIC_AR_20 = "on November 29th, spring 2017 registration continues";
    private final static String STATIC_AR_21 = "on November 30th, spring 2017 registration continues";
    private final static String STATIC_AR_22 = "on December 1st, spring 2017 registration contiues";
    private final static String STATIC_AR_23 = "on December 1st, the Thesis deadline for December 2016 graduates";
    private final static String STATIC_AR_24 = "on December 2nd, spring 2017 registration continues";
    private final static String STATIC_AR_25 = "on December 3rd, spring 2017 registration continues";
    private final static String STATIC_AR_26 = "on December 4th, spring 2017 registration continues";
    private final static String STATIC_AR_27 = "on December 5th, spring 2017 registration continues";
    private final static String STATIC_AR_28 = "on December 6th, spring 2017 Registration continues";
    private final static String STATIC_AR_29 = "on December 7th, spring 2017 registration continues";
    private final static String STATIC_AR_30 = "on December 8th, spring 2017 registration continues";
    private final static String STATIC_AR_31 = "on December 9th, Spring 2017 registration continues";
    private final static String STATIC_AR_32 = "on December 9th, Last day for full term withdrawl with no refund";
    private final static String STATIC_AR_33 = "on December 10th, Spring 2017 registration continues";
    private final static String STATIC_AR_34 = "on December 10th, Withdrawls starting this dare considered Retroactive and need to follow the University's withdrawl policy";
    private final static String STATIC_AR_35 = "on December 11th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_36 = "on December 12th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_37 = "on December 12th, Final week begins";
    private final static String STATIC_AR_38 = "on December 13th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_39 = "on December 13th, Finals week continues";
    private final static String STATIC_AR_40 = "on December 14th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_41 = "on December 14th, Finals week continues";
    private final static String STATIC_AR_42 = "on December 15th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_43 = "on December 15th, Finals week continues";
    private final static String STATIC_AR_44 = "on Decmeber 16th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_45 = "on December 16th, Finals week continues";
    private final static String STATIC_AR_46 = "on December 17th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_47 = "on December 18th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_48 = "on December 19th, Registration for Spring 2017 continues";

    private final static String STATIC_AR_49 = "on December 20th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_50 = "on December 21st, Registration for Spring 2017 continues";
    private final static String STATIC_AR_51 = "on December 22nd, Registration for Spring 2017 continues";
    private final static String STATIC_AR_52 = "on December 23rd, Registration for Spring 2017 continues";
    private final static String STATIC_AR_53 = "on December 24th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_54 = "on December 25th, Registration for Spring 2017 continues";
    // *** Is there supposed to be a STATIC_AR_55?
    ///////// lol no i just dont know how to count - Ashley
    private final static String STATIC_AR_55 = "on December 26th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_56 = "on December 27th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_57 = "on December 28th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_58 = "on December 29th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_59 = "on December 30th, Registration for Spring 2017 continues";
    private final static String STATIC_AR_60 = "on December 31st, Registration for Spring 2017 continues";
    //** Add more STATIC_AR_# (academic responses)
    private final static String STATIC_AR_61 = "on January 1st, fee payment deadline is due, for all students participating in registration from 11/14 to 12/31";
    private final static String STATIC_AR_62 = " on January 1st, registration is closed for financial aid processing";
    private final static String STATIC_AR_63 = " on January 2nd, registration is closed for financial aid processing";
    private final static String STATIC_AR_64 = " on January 3rd, registration is closed for financial aid processing";
    private final static String STATIC_AR_65 = " on January 4th, registration is closed for financial aid processing";
    private final static String STATIC_AR_66 = " on January 5th, registration is closed for financial aid processing";
    private final static String STATIC_AR_67 = " on January 6th, registration is closed for financial aid processing";
    private final static String STATIC_AR_68 = " on January 7th, registration is closed for financial aid processing";
    private final static String STATIC_AR_69 = " on January 8th, registration is closed for financial aid processing";
    private final static String STATIC_AR_70 = " on January 9th, registration is closed for financial aid processing";
    private final static String STATIC_AR_71 = " on January 10th, registration is closed for financial aid processing";
    private final static String STATIC_AR_72 = " on January 11th, registration is closed for financial aid processing";
    private final static String STATIC_AR_73 = " on January 12th, registration is closed for financial aid processing";
    private final static String STATIC_AR_74 = " on January 13th, registration is closed for financial aid processing";
    private final static String STATIC_AR_75 = " on January 14th, registration is closed for financial aid processing";
    private final static String STATIC_AR_76 = " on January 15th, registration is closed for financial aid processing";
    private final static String STATIC_AR_77 = "on January 15th, Scholarship application deadline for Fall 2017";
    private final static String STATIC_AR_78 = " on January 16th, registration is closed for financial aid processing";
    private final static String STATIC_AR_79 = "on January 16th, The campus is closed because it is Martin Luther King juniors birthday"; //MIGHT NEED TO FIX WORDING DEPENDING ON HOW ALEXA SAYS IT
    private final static String STATIC_AR_80 = " on January 17th, registration is closed for financial aid processing";
    private final static String STATIC_AR_81 = " on January 18th, registration is closed for financial aid processing";
    private final static String STATIC_AR_82 = "on January 19th, Faculty work day";
    private final static String STATIC_AR_83 = "on January 19th, late registration and add drop period beings";
    private final static String STATIC_AR_84 = "on January 19th, Waitlist resumes running";
    private final static String STATIC_AR_85 = "on January 20th, late registration and add drop period continues";
    private final static String STATIC_AR_86 = "on January 20th, Faculty work day, school and department meetings";
    private final static String STATIC_AR_87 = "on January 21st, late registration and add drop period continues";
    private final static String STATIC_AR_88 = "on January 22nd, late registration and add drop period continues";
    private final static String STATIC_AR_89 = "on January 22nd, last day to cancel registration with full refund of fees";
    private final static String STATIC_AR_90 = "on January 23rd, late registration and add drop period continues";
    private final static String STATIC_AR_91 = "on January 23rd, instruction begins";
    private final static String STATIC_AR_92 = "on January 24th, late registration and add drop period continues";
    private final static String STATIC_AR_93 = "on January 25th, late registration and add drop period continues";
    private final static String STATIC_AR_94 = "on January 26th, late registration and add drop period continues";
    private final static String STATIC_AR_95 = "on January 27th, late registration and add drop period continues";
    private final static String STATIC_AR_96 = "on January 28th, late registration and add drop period continues";
    private final static String STATIC_AR_97 = "on January 29th, late registration and add drop period continues";
    private final static String STATIC_AR_98 = "on January 30th, late registration and add drop period continues";
    private final static String STATIC_AR_99 = "on January 31st, late registration and add drop period continues";
    private final static String STATIC_AR_100 = "on February 1st, late registration and add drop period continues";

    //*** Will need to either get rid of STATIC_SR_1,2, and 3 and fix the listThreeEventsAS function or rename all the other STATIC_SR_’s
    //////Fixed it :)
    private final static String STATIC_SR_1 = "Women's volleyball on November 17th at C C A A Championship.";
    private final static String STATIC_SR_2 = "Soccer game on November 18th at N C A A Division 2 Championship Tournament (Day 1 of Third Round and Quarter final). For both Women and Men.";
    private final static String STATIC_SR_3 = "Women''s volleyball game November 18th at C C A A Championship Tournament, in San Bernardino.";
    private final static String STATIC_SR_4 = "Women''s basketball game November 18th against Dixie State, in Chico, at 5:30 P M";
    private final static String STATIC_SR_5 = "Men''s basketball game, November 18th against Saint Martin''s, at Sonoma State, at 7:30 P M";
    private final static String STATIC_SR_6 = "Soccer game November 19th at N C A A Division 2 Championship Tournament (Day 2 of Third Round and Quarter final). For both Women and Men.";
    private final static String STATIC_SR_7 = "Women''s volleyball game November 19th at C C A A Championship Tournament, in San Bernardino";
    private final static String STATIC_SR_8 = "Women''s cross country meet November 19th at N C A A Division 2 Championship, in Saint Leo, Florida";
    private final static String STATIC_SR_9 = "Women''s basketball game November 19th against Western Oregon at 5:30 P M, at Chico";
    private final static String STATIC_SR_10 = "Men''s basketball tournament November 19th against San Bernardino and Saint Martins at 5:30 P M, at Sonoma State";
    private final static String STATIC_SR_11 = "Men''s basketball game November 19th against Academy of Art at 7:30 P M, at Sonoma State";
    private final static String STATIC_SR_12 = "Soccer game at N C A A Division 2 Championship Tournament November 20th (Day 3 of Third Round and Quarter final). For both Women and Men.";
    private final static String STATIC_SR_13 = "Soccer game at N C A A Division 2 Championship Tournament November 21st (Day 4 of Third Round and Quarter final). For both Women and Men.";
    private final static String STATIC_SR_14 = "Men''s basketball game November 25th at Dixie State at 6:30 P M";
    private final static String STATIC_SR_15 = "Men''s basketball game November 26th against Westminster College at 4 P M, at Dixie State";
    private final static String STATIC_SR_16 = "Women''s basketball game November 29th at Menlo at 6 P M";
    private final static String STATIC_SR_17 = "Men''s basketball game December 1st at U C Santa Barbara at 7 P M";
    private final static String STATIC_SR_18 = "Basketball game December 3rd at Cal State L A. Women at 5:30 P M and Men at 7:30 P M";
    private final static String STATIC_SR_19 = "Basketball game December 9th against San Francisco State at College of Marin. Women at 5:30 P M and Men at 7:30 P M";
    private final static String STATIC_SR_20 = "Basketball game December 10th against Cal State Monterey Bay at College of Marin. Women at 5:30 P M and Men at 7:30 P M";
    private final static String STATIC_SR_21 = "Basketball game December 17th at Chico State. Women at 5:30 P M and Men at 7:30 P M";
    private final static String STATIC_SR_22 = "Basketball game December 30th against Cal Poly Pomona at Santa Rosa Junior College. Women at 5:30 P M and Men at 7:30 P M";
    private final static String STATIC_SR_23 = "Basketball game December 31st against Cal State East Bay at Santa Rosa Junior College. Women at 5:30 P M and Men at 7:30 P M";
    private final static String STATIC_SR_24 = "Womens basketball game on January 3rd against Academy of Art, Time will be announced";
    private final static String STATIC_SR_25 = "Basketball game on January 6th at Stanislaus state women at 5:30 P M, men at 7:30 P M";
    private final static String STATIC_SR_26 = "basketball game on January 13th against Cal State San Bernardino at Sonoma State, women at 5:30 P M, men at 7:30 P M";
    private final static String STATIC_SR_27 = "basketball game on January 14th against U C San Diego at Sonoma State, Women at 5:30 P M, men at 7:30 P M";
    private final static String STATIC_SR_28 = "Mens tennis on january 16th at Saint Mary's at 2 P M";
    private final static String STATIC_SR_29 = "Womens water polo on January 21st against U C San diego in Santa Barbara at 12 P M";
    private final static String STATIC_SR_30 = "Mens tennis on January 21st at Santa Clara at 2 P M";
    private final static String STATIC_SR_31 = "Womens Water polo on January 21st against California Baptist in Santa Barbara at 4 P M";
    private final static String STATIC_SR_32 = "basketball game at on January 21st at San Francisco State, women at 5:30 P M, men at 7:30 P M";
    private final static String STATIC_SR_33 = "Womens water polo on January 22nd against California State University Northridge in Santa Barbara at 9:10 A M";
    private final static String STATIC_SR_34 = "womens water polo on January 22nd against Marist in Santa Barbara at 1:30 P M";
    private final static String STATIC_SR_35 ="basketball game on January 26 at Cal State Dominguez Hills, women at 5:30 P M, men at 7:30 P M";
    private final static String STATIC_SR_36 = "Womens Tennis on January 27th against Foothill at Sonoma State at 1:00 P M";
    private final static String STATIC_SR_37 = "Mens tennis on January 27th against Santa Rosa Junior College at Sonoma State at 1 P M";
    private final static String STATIC_SR_38 = "Womens tennis on January 28th at Santa Clara at 10 A M";
    private final static String STATIC_SR_39 = "basketball games on January 28th at Cal State Marcos, women at 5:30 P M, men ar 7:30 P M";
    private final static String STATIC_SR_40 = "Mens tennis on February 3rd against Holy Names at Sonoma State at 11 A M";
    private final static String STATIC_SR_41 = "womens tennis on February 3rd at Holy Names at 11 A M";
    private final static String STATIC_SR_42 = "baseball game on February 3rd against San Francisco State at Sonoma State at 11 A M";
    private final static String STATIC_SR_43 = "baseball game on February 3rd againts San Francisco State at Sonoma State at 2 P M";
    private final static String STATIC_SR_44 = "Softball game on February 3rd against Montana State Billings in Las Vegas at 4:30 P M";
    private final static String STATIC_SR_45 = "basketball game on February 3rd against Chico State at Sonoma State, Women at 5:30 P M, men at 7:30 P M";
    private final static String STATIC_SR_46 = "Softball game on February 3rd against Sioux Falls in Las Vegas at 7 P M";
    private final static String STATIC_SR_47 = "Womens water polo on February 4th at Santa Clara, time is to be announced";
    private final static String STATIC_SR_48 = "womens water polo on February 4th against Fresno Pacific in Santa Clara, time is to be announced";
    private final static String STATIC_SR_49 = "baseball game on February 4th at San Francisco state at 11 A M";
    private final static String STATIC_SR_50 = "mens tennis on February 4th against Southern Utah at Sonoma State at 11 A M";
    private final static String STATIC_SR_51 = "softball game on February 4th against Hawaii Hilo in Las Vegas at 11:30 A M";
    private final static String STATIC_SR_52 = "baseball game on February 4th at San Francisco State at 2 P M";
    private final static String STATIC_SR_53 = "Softball game on February 4th against Texas Woman's University in Las Vegas at 4:30 P M";
    private final static String STATIC_SR_54 = "basketball on February 4th against Stanislaus State at sonoma State, women at 5:30 P M, men at 7:30 P M";
    private final static String STATIC_SR_55 = "Softball game on February 5th against Western New Mexico in Las Vegas at 2 P M ";
    private final static String STATIC_SR_56 = "Womens Tennis on February 5th at U C Davis at 2:30 P M";
    private final static String STATIC_SR_57 = "Mens Golf on February 6th are participating in the Fujikura Invite";
    private final static String STATIC_SR_58 = "womens Golf on February 6th  are participating in the P L N U Reach";
    private final static String STATIC_SR_59 = "mens golf on February 7th are particparing in the Fujikura Invite";
    private final static String STATIC_SR_60 = "womens golf on February 7th are participating in the P L N U Reach";
    private final static String STATIC_SR_61 = "baseball game on February 7th against Holy Names at Sonoma State at 1  P M";
    private final static String STATIC_SR_62 = "Softball game on Febraury 10th at Cal State Monterey Bay at 12 P M ";
    private final static String STATIC_SR_63 = "Softball game on February 10th at Cal State Monterey Bay at 2 P M";
    private final static String STATIC_SR_64 = "basketball game on February 10th at Cal State East Bay, women at 5:30 P M, men at 7:30 P M";
    private final static String STATIC_SR_65 = "womens water polo on February 11th in the Triton Invitational in La Jolla, time to be announced";
    private final static String STATIC_SR_66 = "womens water polo on February 11th in the Triton Invitational in La Jolla, time to be announced";
    private final static String STATIC_SR_67 = "Softball game on February 11th at Cal State Monterey Bay at 12 P M";
    private final static String STATIC_SR_68 = "Womens tennis on February 11th against Mills College at Sonoma State at 12 P M";
    private final static String STATIC_SR_69 = "softball game on February 11th at Cal State Monetery Bay at 2 P M";
    private final static String STATIC_SR_70 = "basketball game on February 11th at Cal State Monterey Bay, women at 5:30  P M, men at 7:30 P M";
    //** Add more STATIC_SR_# (sport responses)

    //Session state storage key
    private final static String SESSION_SSUCALENDAR_STATE = "GeneralCalendarState";
    private final static String SESSION_ACADEMICCALENDAR_STATE = "AcademicCalendarState";
    private final static String SESSION_SPORTCALENDAR_STATE = "SportsCalendarState";
    private final static String SESSION_ACADEMICTYPE_STATE = "AcademicType";
    private final static String SESSION_SPORTTYPE_STATE = "SportType";

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
	    response = listThreeEventsGASIntent(GENERAL_CALENDAR, intentReq, session);
	}
	else if (INTENT_SSUACADEMICC.equals(intentName)) { //#2 //Works
	    response = handleWhatSubcalendarType(intentReq, session, ACADEMIC_CALENDAR);
	}
	else if (INTENT_SSUSPORTSC.equals(intentName)) { //#3 //Works
	    response = handleWhatSubcalendarType(intentReq, session, SPORTS_CALENDAR);
	}
	// 1st Continuation for GenCal
	else if(INTENT_WHICHCALENDARA.equals(intentName)) { //#4 //Response1/3 to #1 //Works
	    response = handleWhatSubcalendarType(intentReq, session, ACADEMIC_CALENDAR);
	}
	else if(INTENT_WHICHCALENDARS.equals(intentName)) { //#5 //Response2/3 to #1 //Works
	    response = handleWhatSubcalendarType(intentReq, session, SPORTS_CALENDAR);
	}
	// Getting into specific Calendars
	else if("AMAZON.YesIntent".equals(intentName)) {
	    // Continuing Specific Calendars
	    if (session.getAttribute(SESSION_ACADEMICCALENDAR_STATE) != null && STATE_WAITING_CONTINUE.compareTo((Integer)session.getAttribute(SESSION_ACADEMICCALENDAR_STATE)) == 0) {
		response = listThreeEventsASIntent(ACADEMIC_CALENDAR, intentReq, session);
	    }
	    else if (session.getAttribute(SESSION_SPORTCALENDAR_STATE) != null && STATE_WAITING_CONTINUE.compareTo((Integer)session.getAttribute(SESSION_SPORTCALENDAR_STATE)) == 0){
		response = listThreeEventsASIntent(SPORTS_CALENDAR, intentReq, session);
	    }
	    // Continuing Specific Subcalendars
	    else {
		response = newTellResponse("Did you even listen to my question?", false);
	    }
	}
	else if("AMAZON.NoIntent".equals(intentName)) {
	    if (session.getAttribute(SESSION_SSUCALENDAR_STATE) != null && STATE_WAITING_WHATCALENDAR.compareTo((Integer)session.getAttribute(SESSION_SSUCALENDAR_STATE)) == 0) { //#7a //Response3/3 to #1 //Works
		response = newTellResponse("Have a nice day", false);
	    }
	    else if (session.getAttribute(SESSION_ACADEMICTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer)session.getAttribute(SESSION_ACADEMICTYPE_STATE)) == 0) {
		response = listThreeEventsASIntent(ACADEMIC_CALENDAR, intentReq, session);
	    }
	    else if (session.getAttribute(SESSION_SPORTTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer)session.getAttribute(SESSION_SPORTTYPE_STATE)) == 0) {
		response = listThreeEventsASIntent(SPORTS_CALENDAR, intentReq, session);
	    }
	    else if ((session.getAttribute(SESSION_ACADEMICCALENDAR_STATE) != null && STATE_WAITING_CONTINUE.compareTo((Integer)session.getAttribute(SESSION_ACADEMICCALENDAR_STATE)) == 0) || (session.getAttribute(SESSION_SPORTCALENDAR_STATE) != null && STATE_WAITING_CONTINUE.compareTo((Integer)session.getAttribute(SESSION_SPORTCALENDAR_STATE)) == 0)) {
		response = newTellResponse("Have a nice day", false);
	    }
	    else {
		response = newTellResponse("Did you even listen to my question?", false);
	    }
	}
	// Getting into specific Subcalendars
	else if (INTENT_ACADEMICTYPEH.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "holiday");
	}
	else if (INTENT_ACADEMICTYPEAD.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "academicdate");
	}
	else if (INTENT_ACADEMICTYPEFD.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "financialdeadline");
	}
	else if (INTENT_SPORTTYPES.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "soccer");
	}
	else if (INTENT_SPORTTYPEV.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "volleyball");
	}
	else if (INTENT_SPORTTYPECC.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "crosscountry");
	}
	else if (INTENT_SPORTTYPEG.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "golf");
	}
	else if (INTENT_SPORTTYPET.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "tennis");
	}
	else if (INTENT_SPORTTYPEB.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "basketball");
	}
	else if (INTENT_SPORTTYPEBB.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "baseball");
	}
	else if (INTENT_SPORTTYPETF.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "trackandfield");
	}
	else if (INTENT_SPORTTYPEWP.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "waterpolo");
	}
	else if (INTENT_SPORTTYPESB.equals(intentName)) {
	    response = listNextSpecificSubcalendarEvent(intentReq, session, "softball");
	}
	else{
	    response = newTellResponse("Fail.", false);
	}
	return response;
    }
    
    private SpeechletResponse listThreeEventsGASIntent(int calendar_type, IntentRequest intentreq, Session session){ //#1 //Works
	SpeechletResponse response = null;
	if (calendar_type == GENERAL_CALENDAR){
	    response = newAskResponse(STATIC_AR_1 + ","  + STATIC_SR_1 + "," + STATIC_AR_2 + "," + "What calendar did you want specifically?", false, "What calendar did you want specifically?", false);
	    session.setAttribute(SESSION_SSUCALENDAR_STATE, STATE_WAITING_WHATCALENDAR);
	}
	else {
	    response = newTellResponse("I don't have any information about that.", false);
	}
	return response;
    }
    private SpeechletResponse listThreeEventsASIntent(int calendar_type, IntentRequest intentreq, Session session) {
	SpeechletResponse response = null;
	if (session.getAttribute(SESSION_ACADEMICTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer)session.getAttribute(SESSION_ACADEMICTYPE_STATE)) == 0){
	    response = newAskResponse(STATIC_AR_1 + "," + STATIC_AR_2 + "," + STATIC_AR_3 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    session.removeAttribute(SESSION_ACADEMICTYPE_STATE);
	    session.setAttribute(SESSION_ACADEMICCALENDAR_STATE, STATE_WAITING_CONTINUE);
	}
	else if (session.getAttribute(SESSION_SPORTTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer)session.getAttribute(SESSION_SPORTTYPE_STATE)) == 0){
	    response = newAskResponse(STATIC_SR_1 + "," + STATIC_SR_2 + "," + STATIC_SR_3 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    session.setAttribute(SESSION_SPORTCALENDAR_STATE, STATE_WAITING_CONTINUE);
	}
	//******* HARD CODED IN, IF IT FAILS ITS PROBABLY BECAUSE OF THIS SECTION. IT MIGHT NOT BE RIGHT - ASHLEY ******************/////
	else if (session.getAttribute(SESSION_ACADEMICCALENDAR_STATE) != null && STATE_WAITING_CONTINUE.compareTo((Integer)session.getAttribute(SESSION_ACADEMICCALENDAR_STATE)) == 0) {
	    if (continue_counter == 1) {
		response = newAskResponse(STATIC_AR_4 + "," + STATIC_AR_5 + "," + STATIC_AR_6 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 2) {
		response = newAskResponse(STATIC_AR_7 + "," + STATIC_AR_8 + "," + STATIC_AR_9 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 3) {
		response = newAskResponse(STATIC_AR_10 + "," + STATIC_AR_11 + "," + STATIC_AR_12 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 4) {
		response = newAskResponse(STATIC_AR_13 + "," + STATIC_AR_14 + "," + STATIC_AR_15 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 5) {
		response = newAskResponse(STATIC_AR_16 + "," + STATIC_AR_17 + "," + STATIC_AR_18 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 6) {
		response = newAskResponse(STATIC_AR_19 + "," + STATIC_AR_20 + "," + STATIC_AR_21 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 7) {
		response = newAskResponse(STATIC_AR_22 + "," + STATIC_AR_23 + "," + STATIC_AR_24 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 8) {
		response = newAskResponse(STATIC_AR_25 + "," + STATIC_AR_26 + "," + STATIC_AR_27 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 9) {
		response = newAskResponse(STATIC_AR_28 + "," + STATIC_AR_29 + "," + STATIC_AR_30 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 10) {
		response = newAskResponse(STATIC_AR_31 + "," + STATIC_AR_32 + "," + STATIC_AR_33 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 11) {
		response = newAskResponse(STATIC_AR_34 + "," + STATIC_AR_35 + "," + STATIC_AR_36 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 12) {
		response = newAskResponse(STATIC_AR_37 + "," + STATIC_AR_38 + "," + STATIC_AR_39 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 13) {
		response = newAskResponse(STATIC_AR_40 + "," + STATIC_AR_41 + "," + STATIC_AR_42 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 14) {
		response = newAskResponse(STATIC_AR_43 + "," + STATIC_AR_44 + "," + STATIC_AR_45 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 15) {
		response = newAskResponse(STATIC_AR_46 + "," + STATIC_AR_47 + "," + STATIC_AR_48 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 16) {
		response = newAskResponse(STATIC_AR_49 + "," + STATIC_AR_50 + "," + STATIC_AR_51 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 17) {
		response = newAskResponse(STATIC_AR_52 + "," + STATIC_AR_53 + "," + STATIC_AR_54 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 18) {
		response = newAskResponse(STATIC_AR_55 + "," + STATIC_AR_56 + "," + STATIC_AR_57 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 19) {
		response = newAskResponse(STATIC_AR_58 + "," + STATIC_AR_59 + "," + STATIC_AR_60 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 20) {
		response = newAskResponse(STATIC_AR_61 + "," + STATIC_AR_62 + "," + STATIC_AR_63 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 21) {
		response = newAskResponse(STATIC_AR_64 + "," + STATIC_AR_65 + "," + STATIC_AR_66 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 22) {
		response = newAskResponse(STATIC_AR_67 + "," + STATIC_AR_68 + "," + STATIC_AR_69 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 23) {
		response = newAskResponse(STATIC_AR_70 + "," + STATIC_AR_71 + "," + STATIC_AR_72 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 24) {
		response = newAskResponse(STATIC_AR_73 + "," + STATIC_AR_74 + "," + STATIC_AR_75 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 25) {
		response = newAskResponse(STATIC_AR_76 + "," + STATIC_AR_77 + "," + STATIC_AR_78 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 26) {
		response = newAskResponse(STATIC_AR_79 + "," + STATIC_AR_80 + "," + STATIC_AR_81 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 27) {
		response = newAskResponse(STATIC_AR_82 + "," + STATIC_AR_83 + "," + STATIC_AR_84 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 28) {
		response = newAskResponse(STATIC_AR_85 + "," + STATIC_AR_86 + "," + STATIC_AR_87 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 29) {
		response = newAskResponse(STATIC_AR_88 + "," + STATIC_AR_89 + "," + STATIC_AR_90 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 30) {
		response = newAskResponse(STATIC_AR_91 + "," + STATIC_AR_92 + "," + STATIC_AR_93 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 31) {
		response = newAskResponse(STATIC_AR_94 + "," + STATIC_AR_95 + "," + STATIC_AR_96 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 32) {
		response = newAskResponse(STATIC_AR_97 + "," + STATIC_AR_98 + "," + STATIC_AR_99 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else {
		response = newTellResponse("Sorry, but there are no more events recorded in the academic caledar", false);
	    }
	    session.setAttribute(SESSION_ACADEMICCALENDAR_STATE, STATE_WAITING_CONTINUE); //** this needs to stay out of the if statements
	}
	else if (session.getAttribute(SESSION_SPORTCALENDAR_STATE) != null && STATE_WAITING_CONTINUE.compareTo((Integer)session.getAttribute(SESSION_SPORTCALENDAR_STATE)) == 0) {
	    if (continue_counter == 1) {
		response = newAskResponse(STATIC_SR_4 + "," + STATIC_SR_5 + "," + STATIC_SR_6 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 2) {
		response = newAskResponse(STATIC_SR_7 + "," + STATIC_SR_8 + "," + STATIC_SR_9 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 3) {
		response = newAskResponse(STATIC_SR_10 + "," + STATIC_SR_11 + "," + STATIC_SR_12 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 4) {
		response = newAskResponse(STATIC_SR_13 + "," + STATIC_SR_14 + "," + STATIC_SR_15 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 5) {
		response = newAskResponse(STATIC_SR_16 + "," + STATIC_SR_17 + "," + STATIC_SR_18 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 6) {
		response = newAskResponse(STATIC_SR_19 + "," + STATIC_SR_20 + "," + STATIC_SR_21 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 7) {
		response = newAskResponse(STATIC_SR_22 + "," + STATIC_SR_23 + "," + STATIC_SR_24 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 8) {
		response = newAskResponse(STATIC_SR_25 + "," + STATIC_SR_26 + "," + STATIC_SR_27 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 9) {
		response = newAskResponse(STATIC_SR_28 + "," + STATIC_SR_29 + "," + STATIC_SR_30 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 10) {
		response = newAskResponse(STATIC_SR_31 + "," + STATIC_SR_32 + "," + STATIC_SR_33 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 11) {
		response = newAskResponse(STATIC_SR_34 + "," + STATIC_SR_35 + "," + STATIC_SR_36 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 12) {
		response = newAskResponse(STATIC_SR_37 + "," + STATIC_SR_38 + "," + STATIC_SR_39 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 13) {
		response = newAskResponse(STATIC_SR_40 + "," + STATIC_SR_41 + "," + STATIC_SR_42 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 14) {
		response = newAskResponse(STATIC_SR_43 + "," + STATIC_SR_44 + "," + STATIC_SR_45 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 15) {
		response = newAskResponse(STATIC_SR_46 + "," + STATIC_SR_47 + "," + STATIC_SR_48 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 16) {
		response = newAskResponse(STATIC_SR_49 + "," + STATIC_SR_50 + "," + STATIC_SR_51 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 17) {
		response = newAskResponse(STATIC_SR_52 + "," + STATIC_SR_53 + "," + STATIC_SR_54 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 18) {
		response = newAskResponse(STATIC_SR_55 + "," + STATIC_SR_56 + "," + STATIC_SR_57 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 19) {
		response = newAskResponse(STATIC_SR_58 + "," + STATIC_SR_59 + "," + STATIC_SR_60 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 20) {
		response = newAskResponse(STATIC_SR_61 + "," + STATIC_SR_62 + "," + STATIC_SR_63 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else if (continue_counter == 21) {
		response = newAskResponse(STATIC_SR_64 + "," + STATIC_SR_65 + "," + STATIC_SR_66 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
		
	    }
	    else if (continue_counter == 22) {
		response = newAskResponse(STATIC_SR_67 + "," + STATIC_SR_68 + "," + STATIC_SR_69 + "," + "Would you like me to continue?", false, "Would you like me to continue?", false);
	    }
	    else {
		response = newTellResponse("Sorry, but there are no more events recorded in the sports calendar", false);
	    }
	    session.setAttribute(SESSION_SPORTCALENDAR_STATE, STATE_WAITING_CONTINUE); //** this needs to stay out of the if statements
	}
	else {
	    response = newTellResponse("I don't have any information on that.", false);
	}
	continue_counter++;
	return response;
    }

    private SpeechletResponse handleWhatSubcalendarType(IntentRequest intentreq, Session session, int calendar_type) {
	SpeechletResponse response = null;
	if (session.getAttribute(SESSION_SSUCALENDAR_STATE) != null && STATE_WAITING_WHATCALENDAR.compareTo((Integer)session.getAttribute(SESSION_SSUCALENDAR_STATE)) == 0){
	    if (calendar_type == ACADEMIC_CALENDAR){
		response = newAskResponse("Did you want to hear about a specific category in the Academic Calendar? Tell me what category", false, "Say holiday, academic date, or financial deadline", false);
		session.removeAttribute(SESSION_SSUCALENDAR_STATE);
		session.setAttribute(SESSION_ACADEMICTYPE_STATE, STATE_WAITING_WHATSUBCALENDAR);
	    }
	    else if (calendar_type == SPORTS_CALENDAR){
		response = newAskResponse("Did you want to hear about a specific sport? Tell me what sport", false, "What sport did you want?", false);
		session.removeAttribute(SESSION_SSUCALENDAR_STATE);
		session.setAttribute(SESSION_SPORTTYPE_STATE, STATE_WAITING_WHATSUBCALENDAR);
	    }
	}
	else if (calendar_type == ACADEMIC_CALENDAR){
	    response = newAskResponse("Did you want to hear about a specific category in the Academic Calendar? Tell me what category", false, "Say holiday, academic date, or financial deadline", false);
	    //session.removeAttribute(SESSION_SSUCALENDAR_STATE);
	    session.setAttribute(SESSION_ACADEMICTYPE_STATE, STATE_WAITING_WHATSUBCALENDAR);
	}
	else if (calendar_type == SPORTS_CALENDAR){
	    response = newAskResponse("Did you want to hear about a specific sport? Tell me what sport", false, "What sport did you want?", false);
	    //session.removeAttribute(SESSION_SSUCALENDAR_STATE);
	    session.setAttribute(SESSION_SPORTTYPE_STATE, STATE_WAITING_WHATSUBCALENDAR);
	}
	else {
	    response = newTellResponse("Fail in handle what sub calendar type function", false);
	}
	return response;
    }
    
    private SpeechletResponse listNextSpecificSubcalendarEvent(IntentRequest intentreq, Session session, String subcalendar_type) { //** make sure all the dates in here are up to date!
	SpeechletResponse response = null;
	if(session.getAttribute(SESSION_ACADEMICTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer)session.getAttribute(SESSION_ACADEMICTYPE_STATE)) == 0){ //*** need to check if all the events are from Nov 15 and on
	    if (subcalendar_type == "academicdate") {
		response = newTellResponse("The next academic deadline is on November 15th, it's the last day to petition to withdraw from a class with a 20 dollar administrative fee", false);
		session.removeAttribute(SESSION_ACADEMICTYPE_STATE);
	    }
	    else if (subcalendar_type == "academicdate") {
		response = newTellResponse("The next academic deadline is on November 15th, Spring 2017 registration begins by appointment", false);
		session.removeAttribute(SESSION_ACADEMICTYPE_STATE);
	    }
	}
	else if(session.getAttribute(SESSION_SPORTTYPE_STATE) != null && STATE_WAITING_WHATSUBCALENDAR.compareTo((Integer)session.getAttribute(SESSION_SPORTTYPE_STATE)) == 0){
	    
	    //*** need to check if all the events are from Nov 15 and on
	    //DONE
	    if (subcalendar_type == "soccer") {
		response = newTellResponse("The next soccer game is on November 18th in a N C A A division 2 championship tournament. Both men and women are playing in this tournament", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	    else if (subcalendar_type == "volleyball") {
		response = newTellResponse("The next volleyball game is at a C C A A championship tournament in San Bernardino on November 17th", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	    else if (subcalendar_type == "basketball") {
		response = newTellResponse("The next Womens basketball game is on November 18th against Dixie State in Chico at 5:30 P M", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	    else if(subcalendar_type == "basketball"){
		response = newTellResponse("The next mens basketball game is on November 18th against Cal State San Bernardino at Sonoma State at 5:30 P M", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	    else if (subcalendar_type == "golf") {
		response = newTellResponse("The next golf tournament is on February 2nd 2017, at Point Loma Nazarene University", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	    else if (subcalendar_type == "tennis") {
		response = newTellResponse("The next tennis match is Mens's tennis on Janurary 16th at 2 P M, at Saint Mary's", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	    else if (subcalendar_type == "baseball") { //** this event needs a date
		response = newTellResponse("The next baseball game is on February 3rd against San Francisco State, at Sonoma State, at 11 A M", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	    else if (subcalendar_type == "trackandfield") {
		response = newTellResponse("The next track and field meet is on February 27th 2016, at Johnny Mathis Invitational, at 9 A M", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	    else if (subcalendar_type == "waterpolo") {
		response = newTellResponse("The next water polo game is on January 21st, against U C San Diego, at Santa Barbara, at 12 P M", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	    else if (subcalendar_type == "softball") {
		response = newTellResponse("The next softball game in on February 5th, against Western New Mexico in Las Vegas at 2 P M", false);
		session.removeAttribute(SESSION_SPORTTYPE_STATE);
	    }
	}
	else {
	    response = newTellResponse("I don't have any information on that", false);
	}
	return response;
    }
}

