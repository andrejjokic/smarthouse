/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.userdevice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MenuBar;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import smarthouse.types.AddEvent;
import smarthouse.types.ChangeEvent;
import smarthouse.types.CreateAlarm;
import smarthouse.types.OldAlarm;
import smarthouse.types.RegisterUser;

/**
 *
 * @author Andrej
 */

public class UserDevice extends JFrame {
    
    public static final int FRAME_WIDTH = 1500;
    public static final int FRAME_HEIGHT= 1000;
    public static final String URI_PLAYER = "http://localhost:8080/CustomerService2/resources/players";
    public static final String URI_USER = "http://localhost:8080/CustomerService2/resources/users";
    public static final String URI_ALARM = "http://localhost:8080/CustomerService2/resources/alarms";
    public static final String URI_PLANNER = "http://localhost:8080/CustomerService2/resources/planners";
    
    private String username;
    private String password;
    private String authorizationHeaderValue;
    private Date prevDate;
    
    public UserDevice() {
        super("User Device");
        setSize(FRAME_WIDTH,FRAME_HEIGHT);
        showLoginRegister();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void main(String[] args) {
        new UserDevice();
    }

    private void addMenus() {
        JMenuBar menuBar = new JMenuBar();
        
        addPlayerMenu(menuBar);
        addAlarmMenu(menuBar);
        addPlannerMenu(menuBar);
        
        setJMenuBar(menuBar);
    }

    private void addPlayerMenu(JMenuBar menuBar) {
        JMenu mainMenu = new JMenu("Player");
        JMenuItem playSong = new JMenuItem("Play song");
        JMenuItem showSongs = new JMenuItem("Show played songs");
      
        playSong.addActionListener(l -> {
            showPlaySongComponents();
        });
        
        showSongs.addActionListener(l -> {
            //Separate thread to do the job because it might take same time
            new Thread() {
                @Override
                public void run() {
                    showPlayedSongsRequest();
                }                
            }.start();
        });
        
        mainMenu.add(playSong);
        mainMenu.add(showSongs);
        menuBar.add(mainMenu);  
    }

    private void showLogin() {
        getContentPane().removeAll();
        setLayout(null);
        
        JLabel usernameL = new JLabel("Username:");
        JLabel passwordL = new JLabel("Password:");
        
        JTextField username = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        
        
        JButton confirm = new JButton("Confirm");
        confirm.setBounds(550, 400, 250, 100);
        
        JPanel usernameP = new JPanel();
        usernameP.add(usernameL);
        usernameP.add(username);
        usernameP.setBounds(550, 100, 250, 100);
        
        JPanel passwordP = new JPanel();
        passwordP.add(passwordL);
        passwordP.add(password);
        passwordP.setBounds(550, 250, 250, 100);
        
        confirm.addActionListener(l -> {
            this.password = new String(password.getPassword());
            this.username = username.getText();
            String usernameAndPassword = this.username + ":" + this.password;
            this.authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString( usernameAndPassword.getBytes() );
         
            Response response = loginRequest();
     
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                new ErrorDialog("Username or password incorrect");
                return;
            }
            
            UserDevice.this.getContentPane().removeAll();
            UserDevice.this.addMenus();
            UserDevice.this.validate();
            UserDevice.this.repaint();
        });
        
        add(usernameP);
        add(passwordP);
        add(confirm);
        
        validate();
        repaint();
    }
    
    private void showRegister() {
        getContentPane().removeAll();
        setLayout(null);
        
        JLabel usernameL = new JLabel("Username:");
        JLabel passwordL = new JLabel("Password:");
        JLabel nameL = new JLabel("Name:");
        JLabel alarmSongNameL = new JLabel("Alarm song name:");
        JLabel alarmSongUriL = new JLabel("Alarm song URL:");
        JLabel homeNameL = new JLabel("Location name:");
        JLabel latitudeL = new JLabel("Latitude:");
        JLabel longitudeL = new JLabel("Longitude:");
        JLabel homeL = new JLabel("Home location:");
        homeL.setBounds(650, 550, 250, 60);
        
        JTextField username = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        JTextField name = new JTextField(40);
        JTextField alarmSongName = new JTextField(40);
        JTextField alarmSongUri = new JTextField(50);
        JTextField homeName = new JTextField(10);
        JTextField latitude = new JTextField(8);
        JTextField longitude = new JTextField(8);
                
        JButton confirm = new JButton("Confirm");
        confirm.setBounds(550, 800, 250, 100);
        
        JPanel usernameP = new JPanel();
        usernameP.add(usernameL);
        usernameP.add(username);
        usernameP.setBounds(550, 150, 250, 90);
        
        JPanel passwordP = new JPanel();
        passwordP.add(passwordL);
        passwordP.add(password);
        passwordP.setBounds(550, 250, 250, 90);
        
        JPanel nameP = new JPanel();
        nameP.add(nameL);
        nameP.add(name);
        nameP.setBounds(450, 50, 450, 90);
        
        JPanel alarmSongNameP = new JPanel();
        alarmSongNameP.add(alarmSongNameL);
        alarmSongNameP.add(alarmSongName);
        alarmSongNameP.setBounds(450, 350, 450, 90);
        
        JPanel alarmSongUriP = new JPanel();
        alarmSongUriP.add(alarmSongUriL);
        alarmSongUriP.add(alarmSongUri);
        alarmSongUriP.setBounds(380, 450, 600, 90);
        
        JPanel latitudeP = new JPanel();
        latitudeP.add(latitudeL);
        latitudeP.add(latitude);
        latitudeP.setBounds(600, 610, 125, 90);
        
        JPanel longitudeP = new JPanel();
        longitudeP.add(longitudeL);
        longitudeP.add(longitude);
        longitudeP.setBounds(750, 610, 125, 90);
        
        JPanel homeP = new JPanel();
        homeP.add(homeNameL);
        homeP.add(homeName);
        homeP.setBounds(420, 610, 125, 90);
        
        confirm.addActionListener(l -> {
            String nameS = name.getText();
            String usernameS = username.getText();
            String passwordS = new String(password.getPassword());
            String alarmName = alarmSongName.getText();
            String alarmUrl = alarmSongUri.getText();
            String home = homeName.getText();
            Double latitudeD = Double.parseDouble(latitude.getText());
            Double longitudeD = Double.parseDouble(longitude.getText());
            
            Response response = registerRequest(new RegisterUser(nameS, usernameS, passwordS, alarmName, alarmUrl, latitudeD, longitudeD,home));
            
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                new ErrorDialog("Can't create user!");
                return;
            }
            
            UserDevice.this.getContentPane().removeAll();
            UserDevice.this.addMenus();
            UserDevice.this.validate();
            UserDevice.this.repaint();
        });
        
        add(nameP);
        add(usernameP);
        add(passwordP);
        add(alarmSongNameP);
        add(alarmSongUriP);
        add(homeL);
        add(homeP);
        add(latitudeP);
        add(longitudeP);
        add(confirm);
        
        validate();
        repaint();
    }
    
    private void showLoginRegister() {
        
        JMenuBar menuBar = new JMenuBar();
        JMenu loginregister = new JMenu("Login/Register");
        JMenuItem register = new JMenuItem("Register");
        JMenuItem login = new JMenuItem("Login");
        
        register.addActionListener(l -> {
            showRegister();
        });
        
        login.addActionListener(l -> {
            showLogin();
        });
        
        loginregister.add(login);
        loginregister.add(register);
        menuBar.add(loginregister);
        setJMenuBar(menuBar);
    }

    private void addAlarmMenu(JMenuBar menuBar) {
       JMenu mainMenu = new JMenu("Alarm");
       
       JMenu setAlarm = new JMenu("Set alarm");       
       JMenuItem setAlarmSong = new JMenuItem("Set alarm song");
       
       JMenuItem newAlarm = new JMenuItem("New alarm");
       JMenuItem oldAlarm = new JMenuItem("Old alarm");
       
       newAlarm.addActionListener(l -> {
          showSetNewAlarmComponents();
       });
       
       oldAlarm.addActionListener(l -> {
           showSetOldAlarmComponents();
       });
       
       setAlarmSong.addActionListener(l->{
          showSetAlarmSongComponents();
       });
       
       setAlarm.add(newAlarm);
       setAlarm.add(oldAlarm);
       mainMenu.add(setAlarm);
       mainMenu.add(setAlarmSong);
       menuBar.add(mainMenu);
    }

    private void addPlannerMenu(JMenuBar menuBar) {
        JMenu mainMenu = new JMenu("Planner");
        
        JMenuItem enterEvent = new JMenuItem("Add new event");
        JMenuItem showEvents = new JMenuItem("Show events");
        
        enterEvent.addActionListener(l->{
            showEnterEventComponents();
        });
        
        showEvents.addActionListener(l->{
            showShowEventsComponents();
        });
        
        mainMenu.add(enterEvent);
        mainMenu.add(showEvents);
        menuBar.add(mainMenu);
    }
    
    private List<AddEvent> parseJSONEvents(String json) {
     
        List<AddEvent> events = new LinkedList<AddEvent>();
        
        JSONArray eventsArr = new JSONArray(json);
        
        for (int i = 0; i < eventsArr.length(); i++) {
            try {
                JSONObject event = eventsArr.getJSONObject(i);
                String name = event.getString("name");                                  //name of event
                String timeS = event.getString("start");               
                timeS = timeS.substring(0,timeS.length()-5);
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date start = spf.parse(timeS);                                          
                Calendar c = Calendar.getInstance();
                c.setTime(start);
                c.add(Calendar.HOUR_OF_DAY, 1);
                start = c.getTime();                                                      //start date of event
                int duration = event.getInt("duration");                                 //duration of event
                String locationName = event.getString("locationName");                  //location name
                double locationLat = event.getDouble("locationLat");                    //location latitude
                double locationLon = event.getDouble("locationLon");                    //location longitude
                
                events.add(new AddEvent(name, start, duration, locationName, locationLat, locationLon));
                
            } catch (ParseException ex) {
                Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return events;
    }
    
    private void showShowEventsComponents() {
        getContentPane().removeAll();
        setLayout(new GridLayout(2, 1));
        
        String json = getEventsRequest();
        List<AddEvent> events = parseJSONEvents(json);
        
        List<String> eventsS = new LinkedList<String>();
        
        for (AddEvent e : events) {
            StringBuilder s = new StringBuilder();
            s.append(e.getStart() + "-");
            s.append(e.getName() + "-");
            s.append(e.getDuration() + "min-");
            s.append(e.getLocationName());
            
            eventsS.add(s.toString());
        }
        
        JList<String> listArea = new JList<String>(eventsS.toArray(new String[eventsS.size()]));
        listArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listArea.setFont(new Font("Serif",Font.BOLD,30));
        JScrollPane listScroller = new JScrollPane();
        listScroller.setViewportView(listArea);
        listArea.setLayoutOrientation(JList.VERTICAL);
        
        JPanel panel = new JPanel(null);                //Size of panel is: 500x1500
        
        JLabel nameL = new JLabel("Event Name:");
        JLabel startL = new JLabel("Start (dd.MM.yyyy HH:mm) :");
        JLabel durationL = new JLabel("Duration(minutes):");
        JLabel locationL = new JLabel("Location Name and Coordinates:");
        
        JTextField nameT = new JTextField(11);
        JTextField startT = new JTextField(15);
        JTextField durationT = new JTextField(11);
        JTextField locationNT = new JTextField(15);
        JTextField locationLatT = new JTextField(13);
        JTextField locationLonT = new JTextField(13);
        
        JButton changeB = new JButton("Change");
        changeB.setBackground(Color.YELLOW);
        JButton deleteB = new JButton("Delete");
        deleteB.setBackground(Color.RED);
        JButton remainderB = new JButton("Set Remainder");
        remainderB.setBackground(Color.BLUE);
        
        nameL.setBounds(130, 50, 100, 30);
        nameT.setBounds(245,50,100,30);
        startL.setBounds(400, 50, 175, 30);
        startT.setBounds(590, 50, 300, 30);
        durationL.setBounds(1000, 50, 125, 30);
        durationT.setBounds(1140, 50, 100, 30);
        locationL.setBounds(130, 200, 200, 30);
        locationNT.setBounds(400, 200, 300, 30);
        locationLatT.setBounds(770, 200, 200, 30);
        locationLonT.setBounds(1050, 200, 200, 30);
        changeB.setBounds(175, 320, 120, 120);
        deleteB.setBounds(675, 320, 120, 120);
        remainderB.setBounds(1175, 320, 120, 120);
        
        
        listArea.addListSelectionListener(l -> {
            AddEvent event = events.get(listArea.getSelectedIndex());
            //AddEvent event = events.get(l.getFirstIndex());
            nameT.setText(event.getName());
            durationT.setText(event.getDuration() + "");
            locationNT.setText(event.getLocationName());
            locationLatT.setText(event.getLocationLat() + "");
            locationLonT.setText(event.getLocationLon() + "");   
            
            Calendar c = Calendar.getInstance();
            c.setTime(event.getStart());
            String startS = String.format("%02d", c.get(Calendar.DAY_OF_MONTH)) + "." + String.format("%02d", (c.get(Calendar.MONTH) + 1)) + "." + String.format("%02d", c.get(Calendar.YEAR)) + " " + String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", c.get(Calendar.MINUTE));
            
            startT.setText(startS);
            this.prevDate = event.getStart();
        });
        
        changeB.addActionListener(l -> {
            try {
                getContentPane().removeAll();
                validate();
                repaint();
                
                String startS = startT.getText();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                
                ChangeEvent change = new ChangeEvent();
                change.setPrevDate(this.prevDate);
                change.setStart(sdf.parse(startS));
                change.setName(nameT.getText());
                change.setDuration(Integer.parseInt(durationT.getText()));
                change.setLocationName(locationNT.getText());
                
                if (!locationNT.getText().equals("")) {
                    change.setLocationLat(Double.parseDouble(locationLatT.getText()));
                    change.setLocationLon(Double.parseDouble(locationLonT.getText()));
                }
                
                new Thread() {
                    @Override
                    public void run() {
                        changeEventRequest(change);
                    }
                    
                }.start();
                
            } catch (ParseException ex) {
                Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        
        deleteB.addActionListener(l -> {
            getContentPane().removeAll();
            validate();
            repaint();
            
            new Thread() {
                @Override
                public void run() {
                    String dateS = startT.getText();
                    if (dateS.equals("")) return;
                    
                    deleteEventRequest(dateS);
                }
                
            }.start();
        });
        
        remainderB.addActionListener(l -> {
            getContentPane().removeAll();
            validate();
            repaint();
            
            new Thread() {
                @Override
                public void run() {
                    String dateS = startT.getText();
                    if (dateS.equals("")) return;
                    
                    setRemainderRequest(dateS);
                }
                
            }.start();
        });
        
        panel.add(nameL);
        panel.add(nameT);
        panel.add(startL);
        panel.add(startT);
        panel.add(durationL);
        panel.add(durationT);
        panel.add(locationL);
        panel.add(locationNT);
        panel.add(locationLatT);
        panel.add(locationLonT);
        panel.add(changeB);
        panel.add(deleteB);
        panel.add(remainderB);
        
        add(listScroller);
        add(panel);

        validate();
        repaint();
    }
    
    private void showEnterEventComponents() {
        getContentPane().removeAll();
        setLayout(null);
        
        JLabel eventNameL = new JLabel("Event name:");
        JLabel startL = new JLabel("Start date (dd.MM.yyyy HH:mm) :");
        JLabel durationL = new JLabel("Duration(minutes):");
        JLabel locationL = new JLabel("Location Name and Coordinates:");
        
        JTextField eventName = new JTextField(11);
        JTextField start = new JTextField(11);
        JTextField duration = new JTextField(11);
        JTextField locationN = new JTextField(11);
        JTextField locationLat = new JTextField(5);
        JTextField locationLon = new JTextField(5);
        
        JButton addB = new JButton("Add");
        
        eventNameL.setBounds(400, 50, 100, 30);
        eventName.setBounds(620, 50, 100, 30);
        startL.setBounds(400, 200, 200, 30);
        start.setBounds(620, 200, 100, 30);
        durationL.setBounds(400, 400, 150, 30);
        duration.setBounds(620, 400, 100, 30);
        locationL.setBounds(200, 600, 200, 30);
        locationN.setBounds(470, 600, 100, 30);
        locationLat.setBounds(640, 600, 100, 30);
        locationLon.setBounds(810, 600, 100, 30);
        addB.setBounds(625, 750, 120, 120);
        
        addB.addActionListener(l -> {
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                final String locName = locationN.getText();
                //final AddEvent addEvent = new AddEvent(eventName.getText(), sdf.parse(start.getText()), Integer.parseInt(duration.getText()), locationN.getText(), Double.parseDouble(locationLat.getText()), Double.parseDouble(locationLon.getText()));
                final AddEvent addEvent;
                
                if (locationN.getText().equals("")) {
                    addEvent = new AddEvent(eventName.getText(), sdf.parse(start.getText()), Integer.parseInt(duration.getText()), "", 0, 0);
                } else {
                    addEvent = new AddEvent(eventName.getText(), sdf.parse(start.getText()), Integer.parseInt(duration.getText()), locationN.getText(), Double.parseDouble(locationLat.getText()), Double.parseDouble(locationLon.getText()));
                }
                
                new Thread() {
                    @Override
                    public void run() {
                        addEventRequest(addEvent);
                    }
                    
                }.start();
                
                UserDevice.this.getContentPane().removeAll();
                UserDevice.this.validate();
                UserDevice.this.repaint();
                
            } catch (ParseException ex) {
                Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        add(eventNameL);
        add(eventName);
        add(startL);
        add(start);
        add(durationL);
        add(duration);
        add(locationL);
        add(locationN);
        add(locationLat);
        add(locationLon);
        add(addB);
        
        validate();
        repaint();
    }

    private void showPlaySongComponents() {
        getContentPane().removeAll();

        JLabel nameL = new JLabel("Name:");
        nameL.setBounds(300, 100, 50, 30);
        
        JTextField name = new JTextField(15);
        name.setBounds(360, 100, 400, 30);
        
        JLabel uriL = new JLabel("URI:");
        uriL.setBounds(300, 200, 30, 30);
        
        JTextField uri = new JTextField(70);
        uri.setBounds(360, 200, 550, 30);
        
        JButton confirm = new JButton("Confirm");
        confirm.setBounds(450, 300, 100, 100);
        
        confirm.addActionListener(l -> {
            getContentPane().removeAll();
            UserDevice.this.validate();
            UserDevice.this.repaint();
            
            String songName = name.getText();
            String uriString = uri.getText();
            
            //Separate thread to do the job because it might take same time
            new Thread() {
                @Override
                public void run() {
                    playSongRequest(songName,uriString);
                }
            }.start();
        });
        
        add(nameL);
        add(name);
        add(uriL);
        add(uri);
        add(confirm);
        
        validate();
        repaint();
    }

    private void  showSetAlarmSongComponents() {
        getContentPane().removeAll();
        setLayout(null);

        JLabel nameL = new JLabel("Name:");
        nameL.setBounds(300, 100, 50, 30);
        
        JTextField nameT = new JTextField(15);
        nameT.setBounds(360, 100, 400, 30);
        
        JLabel uriL = new JLabel("URI:");
        uriL.setBounds(300, 200, 30, 30);
        
        JTextField uriT = new JTextField(70);
        uriT.setBounds(360, 200, 550, 30);
        
        JButton confirm = new JButton("Confirm");
        confirm.setBounds(450, 300, 100, 100);
        
        confirm.addActionListener(l -> {
            getContentPane().removeAll();
            UserDevice.this.validate();
            UserDevice.this.repaint();
            
            //Separate thread to do the job because it might take same time
            new Thread() {
                @Override
                public void run() {
                    setAlarmSongRequest(nameT.getText(), uriT.getText());
                }
            }.start();
        });
        
        add(nameL);
        add(nameT);
        add(uriL);
        add(uriT);
        add(confirm);
        
        validate();
        repaint();
    }
    
    private void showSetNewAlarmComponents() {
        getContentPane().removeAll();
        setLayout(null);
        
        JLabel timeL = new JLabel("Time:");
        timeL.setFont(new Font(null,Font.BOLD,20));
        JLabel perL = new JLabel("Period");
        perL.setFont(new Font(null,Font.BOLD,20));
        JLabel dayL = new JLabel("Day of Week:");
        JLabel hourL = new JLabel("Hour:");
        JLabel minuteL = new JLabel("Minute:");
        JLabel periodL = new JLabel("Period(in days):");
        JLabel weeksL = new JLabel("Number of week's from this one:");
        
        JTextField hour = new JTextField(3);
        JTextField minute = new JTextField(3);
        JTextField periodic = new JTextField(4);
        JTextField weeks = new JTextField(3);
        
        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        JComboBox dayList = new JComboBox(days);
        
        JButton set = new JButton("Set");
        
        JCheckBox periodicC = new JCheckBox("Periodic");
        
        timeL.setBounds(600, 50, 100, 100);
        dayL.setBounds(100, 200, 100, 50);
        dayList.setBounds(200, 200, 100, 50);
        hourL.setBounds(500, 200, 50, 50);
        hour.setBounds(570, 200, 50, 50);
        minuteL.setBounds(860, 200, 50, 50);
        minute.setBounds(930, 200, 50, 50);
        weeksL.setBounds(1100, 200, 200, 50);
        weeks.setBounds(1320, 200, 50, 50);
        perL.setBounds(600, 400, 100, 100);
        periodL.setBounds(300, 520, 100, 50);
        periodic.setBounds(400, 520, 50, 50);
        periodicC.setBounds(760, 520, 100, 50);
        set.setBounds(530, 650, 200, 60);
        
        set.addActionListener(l -> {
            Calendar now = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = 0;
            int period = 0;
            
            switch ((String)dayList.getItemAt(dayList.getSelectedIndex())) {
                case "Monday":
                    dayOfWeek = Calendar.MONDAY;
                    break;
                case "Tuesday":
                    dayOfWeek = Calendar.TUESDAY;
                    break;
                case "Wednesday":
                    dayOfWeek = Calendar.WEDNESDAY;  
                    break;
                case "Thursday":
                    dayOfWeek = Calendar.THURSDAY;
                    break;
                case "Friday":
                    dayOfWeek = Calendar.FRIDAY;
                    break;
                case "Saturday":
                    dayOfWeek = Calendar.SATURDAY;
                    break;
                case "Sunday":
                    dayOfWeek = Calendar.SUNDAY; 
                    break;
            }
            
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.getText()));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minute.getText()));
            calendar.add(Calendar.WEEK_OF_YEAR,Integer.parseInt(weeks.getText()));
            
            if (now.getTimeInMillis() > calendar.getTimeInMillis()) {
                new ErrorDialog("You can't set alarm at already passed time!");
                return;
            }
            
            getContentPane().removeAll();
            validate();
            repaint();
            
            if (periodicC.isSelected()) {
                period = Integer.parseInt(periodic.getText());
            }
            
            final Calendar c = calendar;
            final int p = period;
            final String u = this.username;
            
            new Thread() {
                @Override
                public void run() {
                    Response response = setAlarmRequest(new CreateAlarm(c,-1,p,u));                    
                }
            
            }.start();
        });
        
        add(timeL);
        add(dayL);
        add(dayList);
        add(hourL);
        add(hour);
        add(minuteL);
        add(minute);
        add(perL);
        add(periodL);
        add(periodic);
        add(periodicC);
        add(set);
        add(weeksL);
        add(weeks);
        
        validate();
        repaint();
    }
    
    private List<OldAlarm> parseJSONAlarms(String json) {
        JSONArray arr = new JSONArray(json);
        List<OldAlarm> alarms = new LinkedList<OldAlarm>();
        
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject obj = arr.getJSONObject(i);
                int id = obj.getInt("alarmId");
                int period = obj.getInt("period");
                int active = obj.getInt("active");
                String timeS = obj.getString("time");
                timeS = timeS.substring(0,timeS.length()-5);
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = spf.parse(timeS);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.HOUR_OF_DAY, 1);
                date = c.getTime();
                
                alarms.add(new OldAlarm(date,period,(short)active, id));
            } catch (ParseException ex) {
                Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return alarms;
    }
    
    private void showSetOldAlarmComponents() {
        getContentPane().removeAll();
        setLayout(new GridLayout(1, 2));

        String jsonRep = showAlarmsRequest();
        
        List<OldAlarm> alarms = parseJSONAlarms(jsonRep);
        
        List<String> timesS = new LinkedList<String>();
        
        for (OldAlarm a : alarms) {
            String s = "";
            Calendar c = Calendar.getInstance();
            c.setTime(a.getTime());
            switch (c.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    s += "Monday";
                    break;
                case Calendar.TUESDAY:
                    s += "Tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    s += "Wednesday";
                    break;
                case Calendar.THURSDAY:
                    s += "Thursday";
                    break;
                case Calendar.FRIDAY:
                    s += "Friday";
                    break;
                case Calendar.SATURDAY:
                    s += "Saturday";
                    break;
                case Calendar.SUNDAY:
                    s += "Sunday";
                    break;
            }
            
            s += " - " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + " Period = " + a.getPeriod() + " Active = " + a.getActive();
            timesS.add(s); 
        }
        
        JList<String> listArea = new JList<String>(timesS.toArray(new String[timesS.size()]));
        listArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listArea.setFont(new Font("Serif",Font.ITALIC,30));
        JScrollPane listScroller = new JScrollPane();
        listScroller.setViewportView(listArea);
        listArea.setLayoutOrientation(JList.VERTICAL);
        
        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        
        JLabel weeksL = new JLabel("Number of week's from this one:");
        JTextField weeks = new JTextField(3);
        JButton set = new JButton("Set");
        JPanel weekP = new JPanel(null);
        JCheckBox active = new JCheckBox("Active");
        
        weeksL.setBounds(100, 100, 200, 30);
        weeks.setBounds(320, 100, 100, 30);
        active.setBounds(300, 250, 100, 30);
        set.setPreferredSize(new Dimension(100, 100));
        
        weekP.add(weeksL);
        weekP.add(weeks);
        weekP.add(active);
        
        set.addActionListener(l -> {
            String selected = listArea.getSelectedValue();
            StringTokenizer st = new StringTokenizer(selected);
            String day = st.nextToken();
            st.nextToken();     //-
            String time = st.nextToken();
            StringTokenizer st2 = new StringTokenizer(time, ":");
            int h = Integer.parseInt(st2.nextToken());
            int m = Integer.parseInt(st2.nextToken());
            
            int dayOfWeek;
            
            switch (day) {
                case "Monday":
                    dayOfWeek = Calendar.MONDAY;
                    break;
                case "Tuesday":
                    dayOfWeek = Calendar.TUESDAY;
                    break;
                case "Wednesday":
                    dayOfWeek = Calendar.WEDNESDAY;  
                    break;
                case "Thursday":
                    dayOfWeek = Calendar.THURSDAY;
                    break;
                case "Friday":
                    dayOfWeek = Calendar.FRIDAY;
                    break;
                case "Saturday":
                    dayOfWeek = Calendar.SATURDAY;
                    break;
                default:
                    dayOfWeek = Calendar.SUNDAY; 
                    break;
            }
            
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            c.set(Calendar.HOUR_OF_DAY, h);
            c.set(Calendar.MINUTE, m);
            c.add(Calendar.WEEK_OF_YEAR,Integer.parseInt(weeks.getText()));

            final String u = this.username;
           
            new Thread() {
                @Override
                public void run() {
                    //Response response = setAlarmRequest(new CreateAlarm(c,0,u));
                    OldAlarm a = alarms.get(listArea.getSelectedIndex());
                    //Calendar c = Calendar.getInstance();
                    //c.setTime(a.getTime());
                    
                    if (!active.isSelected() && a.getActive() == 1) {                     
                        turnOffAlarmRequest(new CreateAlarm(c, a.getAlarmId(), a.getPeriod(), u));
                        return;
                    }
                    
                    if (active.isSelected() && a.getActive() == 0) {
                        setAlarmRequest(new CreateAlarm(c, a.getAlarmId(), a.getPeriod(), u));
                    }
                }
            
            }.start();
            
            getContentPane().removeAll();
            validate();
            repaint();
        });
        
        rightPanel.add(weekP);
        rightPanel.add(set);
        add(listScroller);
        add(rightPanel);
        
        validate();
        repaint();
    }
    
    private void playSongRequest(String songName, String uriString) {
        Client client = ClientBuilder.newClient();
        
        Response response = client.target(URI_PLAYER + "/playSong")
                .queryParam("name", songName)
                .queryParam("uri", uriString)
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .get(Response.class);
        
        client.close();
        System.out.println(response.getStatus());
    }

    private Response loginRequest() {
        Client client = ClientBuilder.newClient();
                  
        Response response = client.target(URI_USER + "/login")
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .get(Response.class);
            
        client.close();
        
        return response;
    }
    
    private Response registerRequest(RegisterUser registerUser) {
        Client client = ClientBuilder.newClient();

        Response response = client.target(URI_USER + "/register")
                .request()
                .post(Entity.xml(registerUser.toXML()));
        
        client.close();
        
        System.out.println(response.getStatus());
        
        return response;
    }
    
    private Response showPlayedSongsRequest() {
        Client client = ClientBuilder.newClient();
        
        Response response = client.target(URI_PLAYER + "/playedSongs")
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .get(Response.class);
        
        client.close();
        
        System.out.println(response.getStatus());
        
        return response;
    }
    
    private Response setAlarmSongRequest(String name,String uri) {
        Client client = ClientBuilder.newClient();
        
        Response response = client.target(URI_ALARM + "/setSong")
                .queryParam("name", name)
                .queryParam("uri", uri)
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .put(Entity.xml(""));
        
        client.close();
        
        System.out.println(response.getStatus());
        
        return response;
    }
    
    private Response setAlarmRequest(CreateAlarm alarm) {
        Client client = ClientBuilder.newClient();
        
        Response response = client.target(URI_ALARM + "/setAlarm")
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .post(Entity.xml(alarm.toXML()));
        
        client.close();
        
        System.out.println(response.getStatus());
        return response;
    }
    
    private String showAlarmsRequest() {
        Client client = ClientBuilder.newClient();
     
        Response response = client.target(URI_ALARM)
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .get();
        
        String ret = response.readEntity(String.class);
        
        client.close();

        return ret;
    }
    
    private Response turnOffAlarmRequest(CreateAlarm alarm) {
        Client client = ClientBuilder.newClient();
        
        Response response = client.target(URI_ALARM + "/turnOff")
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .put(Entity.xml(alarm.toXML()));
        
        client.close();
        
        return null;
    }
    
    private Response addEventRequest(AddEvent event) {
        Client client = ClientBuilder.newClient();
        
        Response response = client.target(URI_PLANNER + "/addEvent")
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .post(Entity.xml(event.toXML()));
        
        client.close();
        
        return null;
    }
    
    private String getEventsRequest() {
        Client client = ClientBuilder.newClient();
     
        Response response = client.target(URI_PLANNER + "/getEvents")
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .get();
        
        String ret = response.readEntity(String.class);
        
        client.close();

        return ret;
    }
    
    private Response deleteEventRequest(String date) {
        Client client = ClientBuilder.newClient();
        
        Response response = client.target(URI_PLANNER + "/deleteEvent")
                .queryParam("date", date)
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .delete();
        
        client.close();
        
        return response;
    }
    
    private Response changeEventRequest(ChangeEvent event) {
        Client client = ClientBuilder.newClient();
        
        Response response = client.target(URI_PLANNER + "/changeEvent")
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .put(Entity.xml(event.toXML()));
        
        client.close();

        return response;
    }
    
    private Response setRemainderRequest(String date) {
        Client client = ClientBuilder.newClient();
        
        Response response = client.target(URI_PLANNER + "/setRemainder")
                .queryParam("date", date)
                .request()
                .header("Authorization", this.authorizationHeaderValue)
                .get();
        
        client.close();
        
        return response;
    }
    
    private class ErrorDialog extends JDialog {
        
        public ErrorDialog(String error) {
            super(UserDevice.this);
            setSize(300, 300);
            setLayout(null);
            JLabel text = new JLabel(error);
            text.setBounds(50, 50, 200, 100);
            add(text);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }
    }
}
