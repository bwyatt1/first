package bwyatt.game.client;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import bwyatt.game.common.*;

public class PlayerPanel extends JPanel
{
    private ArrayList<PlayerInfo> otherPlayers;
    private PlayerInfo myInfo;
    private JLabel meLabel;
    private JLabel serverStatus;
    private JPanel otherPlayersPanel;
    private ArrayList<JLabel> otherPlayersLabels;

    public PlayerPanel()
    {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(boxLayout);
        meLabel = new JLabel();
        this.add(meLabel);
        serverStatus = new JLabel("Offline");
        serverStatus.setFont(serverStatus.getFont().deriveFont(Font.ITALIC));
        this.add(serverStatus);
        otherPlayers = new ArrayList<PlayerInfo>();
        otherPlayersPanel = new JPanel();
        this.add(otherPlayersPanel);
        updateOtherPlayers();
        this.setOpaque(false);
    }

    public void updateOtherPlayers()
    {
        otherPlayersPanel.removeAll();
        
        GridBagLayout layout = new GridBagLayout();
        otherPlayersPanel.setLayout(layout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        for (PlayerInfo info : otherPlayers)
        {
            JLabel label = new JLabel(ImageCache.getPlayerIcon(info.getIconID()));
            otherPlayersPanel.add(label, gbc);

            if (info.getShowing() != PlayerInfo.GAME_NONE)
            {
                label = new JLabel(ImageCache.getStatusIcon(info.getShowing()));
                gbc.gridx = 1;
                otherPlayersPanel.add(label, gbc);
            }

            label = new JLabel(info.getName());
            gbc.gridx = 2;
            otherPlayersPanel.add(label, gbc);

            if (info.getStatus() == PlayerInfo.STATUS_ACTIVE)
            {
                label = new JLabel("" + info.getScore());
                gbc.gridx = 3;
                otherPlayersPanel.add(label, gbc);
            }
            gbc.gridy++;
        }
        otherPlayersPanel.revalidate();
        otherPlayersPanel.repaint();
    }

    public void setMe(PlayerInfo myInfo)
    {
        this.myInfo = myInfo;
        meLabel.setText(myInfo.getName());
        meLabel.setIcon(ImageCache.getPlayerIcon(myInfo.getIconID()));
    }

    public void addPlayer(PlayerInfo p)
    {
        otherPlayers.add(p);
        updateOtherPlayers();
    }

    public void removePlayer(PlayerInfo p)
    {
        otherPlayers.remove(p);
        updateOtherPlayers();
    }

    public void updatePlayer(PlayerInfo p)
    {
        if (p == myInfo)
        {
            String text = p.getName();
            if (p.getShowing() == PlayerInfo.GAME_BOGGLE)
                text = text + " Boggle";
            else if (p.getShowing() == PlayerInfo.GAME_2048)
                text = text + " 2048";

            if (p.getStatus() == PlayerInfo.STATUS_ACTIVE)
                text = text + " Active";
            meLabel.setText(text);
            meLabel.setIcon(ImageCache.getPlayerIcon(p.getIconID()));
            return;
        }
        else
        {
            updateOtherPlayers();
        }
    }

    public void setServerStatus(boolean serverStatus)
    {
        if (serverStatus)
            this.serverStatus.setText("Online");
        else
            this.serverStatus.setText("Offline");
    }
}
