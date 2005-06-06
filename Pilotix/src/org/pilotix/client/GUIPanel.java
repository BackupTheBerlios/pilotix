/*
 Pilotix : a multiplayer piloting game.
 Copyright (C) 2003 Pilotix.Org

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.pilotix.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

/**
 * <p>
 * Cette classe crée l'interface graphique du client, à l'exception des classes
 * orientées 3D.
 * </p>
 * 
 * <p>
 * Elle est censée être utilisée conjointement à une instance de Display3D.
 * Nous avons essayé de séparer les composants Java standards et ceux liés à
 * Java3D. Cette classe est le conteneur principal pour les composants Java
 * standards, tandis que Display3D est le conteneur principal pour les
 * composants Java3D.
 * </p>
 * 
 * <p>
 * Techniquement, cette classe crée un JPanel qui contient une barre de menu
 * dans son panel nord, et différents panneaux pour l'interface graphique. Les
 * composants Java3D sont trouvés dans un objet Display3D référencé dans la
 * classe Environment.
 * </p>
 * 
 * <p>
 * <strong>REMARQUE IMPORTANTE SUR L'INTERNATIONALISATION : </strong>
 * </p>
 * <ul>
 * <li>les chaînes de caractères visibles par l'utilisateur ne doivent plus
 * être codées en dur. Elles doivent être définies dans le fichier "i18nClient"
 * du répertoire "properties/" sous la forme : clef=valeur où clef est la
 * chaîne passée en paramêtre de :
 * ResourceBundle.getBundle("i18nClient").getString(" <clef>")</li>
 * <li>selon l'environnement de l'utilisateur, le fichier réellement ouvert
 * sera par exemple "i18nClient_en", "i18nClient_de", etc.</li>
 * <li>on peut forcer la langue avec l'option -Duser.language sur la ligne de
 * commande, par exemple pour l'espéranto : java -Duser.language=eo -jar
 * dist/PilotixClient.jar</li>
 * </ul>
 * 
 * @author Grégoire Colbert
 * @author Loïc Guibart
 * 
 * @see GUI
 * @see Display3D
 * 
 * @.todo Ajouter une zone de texte en bas pour afficher des informations.
 *  
 */

public class GUIPanel extends JPanel implements ActionListener {

    private JPanel northPanel = null;
    private JPanel centerPanel = null;
    private JPanel eastPanel = null;
    private JMenuItem newgameMenuItem = null;
    private JMenuItem endgameMenuItem = null;
    private JMenuItem quitMenuItem = null;
    private JTable tableInfoPlayers = null;
    private DefaultTableModel infoPlayers = null;
    private Object[] infoColumns = null;

    /**
     * Crée un JPanel et le remplit avec des conteneurs selon une disposition
     * de type BorderLayout.
     */
    public GUIPanel() {
        // GUIPanel est le panneau de plus haut niveau : on définit ses
        // propriétés
        setLayout(new BorderLayout());

        // Crée le menu
        add("North", menuBar());

        // Crée et ajoute le panneau central
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        add("Center", centerPanel);

        // Crée et ajoute le panneau de l'Est
        eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setPreferredSize(new Dimension(250, 0));

        // Liste des joueurs présents
        infoColumns = new Object[5];
        infoColumns[0] = ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString("idLabel");
        infoColumns[1] = ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString(
                "positionXLabel");
        infoColumns[2] = ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString(
                "positionYLabel");
        infoColumns[3] = ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString(
                "directionLabel");
        infoColumns[4] = ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString(
                "speedLabel");
        /*
         * infoColumns[1] = ResourceBundle.getBundle("i18nClient")
         * .getString("colorLabel"); infoColumns[2] =
         * ResourceBundle.getBundle("i18nClient") .getString("nameLabel");
         */
        infoPlayers = new DefaultTableModel(infoColumns, 16);
        JTable tableInfoPlayers = new JTable(infoPlayers);
        JScrollPane scrollpane = new JScrollPane(tableInfoPlayers);
        scrollpane.setPreferredSize(new Dimension(0, 100));
        eastPanel.add(scrollpane);
        add("East", eastPanel);
    }


    /**
     * Cette fonction met à jour la partie non-3D du client au cours du jeu,
     * en consultant ClientArea.
     */
    public void update() {
        // On teste si le joueur a stoppé la partie car si c'est le cas,
        // il faut effacer toutes les lignes du tableau et non une seule.
        boolean playerHasQuit = Environment.theClientArea.shipIsNull(
                                  Environment.theClientArea.getOwnShipId());

        // Petite optimisation possible : ne mettre à jour que jusqu'au numéro
        // le plus grand alloué à un joueur (si on a eu au plus 4 joueurs, il
        // ne sert à rien de mettre à jour au-delà de la ligne 4). Mais ce
        // numéro n'est pas forcément égal au nombre de joueurs en cours, car
        // le joueur dont le numéro est le plus grand n'est pas forcément le
        // premier à partir.
        for (int i = 0; i < Environment.theClientArea.getNbMaxShips(); i++) {
            if (Environment.theClientArea.shipIsNull(i) || playerHasQuit) {
                infoPlayers.setValueAt("", i, 0);
                infoPlayers.setValueAt("", i, 1);
                infoPlayers.setValueAt("", i, 2);
                infoPlayers.setValueAt("", i, 3);
                infoPlayers.setValueAt("", i, 4);
            } else {
                infoPlayers.setValueAt(String.valueOf(i), i, 0);
                infoPlayers.setValueAt(
                      String.valueOf(Environment.theClientArea.getShipPosition(i).x),
                      i, 1);
                infoPlayers.setValueAt(
                      String.valueOf(Environment.theClientArea.getShipPosition(i).y),
                      i, 2);
                infoPlayers.setValueAt(
                      String.valueOf(Environment.theClientArea.getShipDirection(i).get()),
                      i, 3);
                      /*
                infoPlayers.setValueAt(
                      String.valueOf(Environment.theClientArea.getShipSpeed(i).get()),
                      i,4);
                      */
            }
        }
    }

    /**
     * Crée une barre de menu.
     *
     * @return la barre de menu pour le client Pilotix.
     */
    private JMenuBar menuBar() {
        // Indispensable sinon les menus sont cachés par le Canvas3D
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        JMenuBar menuBar = new JMenuBar();

        // Le menu "Jeu"
        JMenu gameMenu = new JMenu(ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString(
                "gameMenuLabel"));
        menuBar.add(gameMenu);
        newgameMenuItem = new JMenuItem(ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString(
                "newgameMenuItemLabel"));
        newgameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                ActionEvent.CTRL_MASK));
        newgameMenuItem.setActionCommand("newgame");
        newgameMenuItem.addActionListener(this);
        gameMenu.add(newgameMenuItem);

        endgameMenuItem = new JMenuItem(ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString(
                "endgameMenuItemLabel"));
        endgameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                ActionEvent.CTRL_MASK));
        endgameMenuItem.setActionCommand("endgame");
        endgameMenuItem.addActionListener(this);
        endgameMenuItem.setEnabled(false);
        gameMenu.add(endgameMenuItem);
        gameMenu.addSeparator();

        quitMenuItem = new JMenuItem(ResourceBundle.getBundle(
                Environment.propertiesPath + "i18nClient").getString(
                "quitMenuItemLabel"));
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                ActionEvent.CTRL_MASK));
        quitMenuItem.setActionCommand("quit");
        gameMenu.add(quitMenuItem);
        quitMenuItem.addActionListener(this);
        // Fin du menu "Jeu"

        return menuBar;
    }

    /**
     * Écouteur de boutons : fait des choses en fonction du champ ActionCommand
     * des boutons.
     * 
     * @param evt
     *            l'instance de ActionEvent à traiter.
     */
    public void actionPerformed(ActionEvent evt) {
        String str = evt.getActionCommand();
        if (str.equals("quit")) {
            System.exit(0);
        } else if (str.equals("newgame")) {
            askForConnectionParameters();
        } else if (str.equals("endgame")) {
            endGame();
        }
    }

    /**
     * Ouvre les boîtes de dialogue pour se connecter au serveur,
     * puis si les paramètres sont potentiellement bons (port entier positif),
     * lance ClientMainLoopThread.
     */
    private void askForConnectionParameters() {
        // Boîtes de dialogue pour la connexion au serveur
        Environment.theServerIP = JOptionPane.showInputDialog(
                      ResourceBundle.getBundle(Environment.propertiesPath + "i18nClient")
                                    .getString("enterServerIPMessage"),
                      Environment.theServerIP);
        
        String tmpStringServerPort = null;                
        tmpStringServerPort = JOptionPane.showInputDialog(
                      ResourceBundle.getBundle(Environment.propertiesPath + "i18nClient")
                                    .getString("enterServerPortMessage"),
                      Environment.theServerPort);               
                              
        try {
            Environment.theServerPort = new Integer(tmpStringServerPort); // -> Exception
        
            // Lance la boucle de réception des messages du serveur
            Environment.theClientMainLoopThread = new ClientMainLoopThread();
            Environment.theClientMainLoopThread.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                  Environment.theGUI,
                  ResourceBundle.getBundle(Environment.propertiesPath + "i18nClient")
                                .getString("error_portNotInteger"),
                  ResourceBundle.getBundle(Environment.propertiesPath + "i18nClient")
                                .getString("error"),
                  JOptionPane.ERROR_MESSAGE);                    
        }
    }

    /**
     * Affiche une boîte de dialogue indiquant que la connexion au serveur a échoué.
     * Cette procédure est appelée par ClientMainLoopThread.
     */
    public void displayMessageConnectionRefused() {
        JOptionPane.showMessageDialog(
              Environment.theGUI,
              ResourceBundle.getBundle(Environment.propertiesPath + "i18nClient")
                            .getString("error_cannotConnectToServer"),
              ResourceBundle.getBundle(Environment.propertiesPath + "i18nClient")
                            .getString("error"),
              JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Démarrer la partie. Est appelé par ClientMainLoopThread lorsque la connexion
     * avec le serveur est établie.
     */
    public void beginGame() {
        // Mise à jour de l'interface
        centerPanel.add("Center", Environment.theDisplay3D.getMainCanvas3D());
        eastPanel.add(Environment.theDisplay3D.getMinimapCanvas3D());
        //Environment.theDisplay3D.getMinimapCanvas3D().setSize(100,100);
        this.validate();
    
        // Mise à jour du menu
        newgameMenuItem.setEnabled(false);
        endgameMenuItem.setEnabled(true);
          
        // Association du Canvas3D principal avec la class Controls pour la
        // souris
        Environment.theControls.setMouseComponent(Environment.theDisplay3D
                .getMainCanvas3D());

        // Activation de la récupération d'évènements clavier par Controls
        Environment.theControls.active(true);

        // Ecouteur pour redimensionner la minimap si on redimensionne la
        // fenêtre
        Environment.theDisplay3D.getMinimapCanvas3D().addComponentListener(
                new ComponentAdapter() {

                    public void componentResized(ComponentEvent e) {
                        float xMax = Environment.theClientArea.getXMax();
                        float yMax = Environment.theClientArea.getYMax();
                        double w = Environment.theDisplay3D
                                .getMinimapCanvas3D().getWidth();
                        Environment.theDisplay3D.getMinimapCanvas3D().setSize(
                                (int) w, (int) (w * (yMax / xMax)));
                    }
                });
    }

    /**
     * Finir la partie. Appelé par l'écouteur de boutons, actionPerformed().
     */
    private void endGame() {
        // Mise à jour de l'interface
        centerPanel.remove(Environment.theDisplay3D.getMainCanvas3D()); // En
                                                                        // premier...
        eastPanel.remove(Environment.theDisplay3D.getMinimapCanvas3D());
        newgameMenuItem.setEnabled(true);
        endgameMenuItem.setEnabled(false);

        // Arrêt de la boucle de réception des messages du serveur
        Environment.theClientMainLoopThread.endGame();

        // Un peu de ménage
        System.gc();
    }
}
