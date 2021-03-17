/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.player;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import smarthouse.entities.Songplayed;
import smarthouse.entities.SongplayedPK;
import smarthouse.messagetypes.PlayerMessage;

/**
 *
 * @author Andrej
 */
public class Player {
    
    private static Player player;
   
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connFactory;
    
    @Resource(lookup = "playerQueue")
    private static Queue playerQueue;
            
    private Player() {}
    
    public static Player getPlayer() {
        if (player == null) player = new Player();
        return player;
    }
  
    private void playSong(Integer userId,String name,String uriString) {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlayerPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            URI uri = new URI(uriString);
            java.awt.Desktop.getDesktop().browse(uri);

            Songplayed sp = new Songplayed(userId, name);
            
            if (em.find(Songplayed.class,new SongplayedPK(userId, name)) == null) {
                em.getTransaction().begin();
                em.persist(sp);
                em.getTransaction().commit();
            }
            
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            
            em.close();
            emf.close();
        }
    }
    
    private void showPlayedSongs(Integer userId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlayerPU");
        EntityManager em = emf.createEntityManager();
        
        List<String> songs = em.createQuery("SELECT sp.songplayedPK.song FROM Songplayed sp WHERE sp.songplayedPK.idUsr = :userId", String.class).setParameter("userId", userId).getResultList();
        
        new ShowSongs(songs);
        
        em.close();
        emf.close();
    }
    
    public static void main(String[] args) {
        Player player = getPlayer();
        JMSConsumer consumer = connFactory.createContext().createConsumer(playerQueue);
        //JMSConsumer consumer = context.createConsumer(playerQueue);
        
        consumer.setMessageListener(m -> {
            try {
                PlayerMessage msg = m.getBody(PlayerMessage.class);
                
                if (msg.getType().ordinal() == PlayerMessage.Type.PLAY_SONG.ordinal()) {
                    player.playSong(msg.getUserId(), msg.getName(), msg.getUri());
                } else {
                    player.showPlayedSongs(msg.getUserId());
                }
            } catch (JMSException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        while (true);
    }
    
    private static class ShowSongs extends JFrame {

        public ShowSongs(List<String> songs){
            super("Played songs");
            setSize(700,700);
            displaySongs(songs);
            setVisible(true);
        }

        private void displaySongs(List<String> songs) {
            setLayout(new BorderLayout());
            
            JList<String> listArea = new JList<String>(songs.toArray(new String[songs.size()]));
            listArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listArea.setFont(new Font("Serif",Font.ITALIC,14));
            JScrollPane listScroller = new JScrollPane();
            listScroller.setViewportView(listArea);
            listArea.setLayoutOrientation(JList.VERTICAL);
            
            add(listScroller,BorderLayout.CENTER);
            add(new JLabel("Songs played:"),BorderLayout.NORTH);
        }
    }
}
