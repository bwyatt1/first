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
    }

    public void updateOtherPlayers()
    {
        otherPlayersPanel.removeAll();
        BoxLayout layout = new BoxLayout(otherPlayersPanel, BoxLayout.Y_AXIS);
        otherPlayersPanel.setLayout(layout);
        otherPlayersLabels = new ArrayList<JLabel>();
        for (PlayerInfo info : otherPlayers)
        {
            JLabel label = new JLabel(ImageCache.getPlayerIcon(info.getIconID()));
            label.setText(info.getName());
            otherPlayersPanel.add(label);
            otherPlayersLabels.add(label);
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

        int i = 0;
        for (PlayerInfo info : otherPlayers)
        {
            if (info == p)
            {
                String text = info.getName();
                if (info.getShowing() == PlayerInfo.GAME_BOGGLE)
                    text = text + " Boggle";
                else if (info.getShowing() == PlayerInfo.GAME_2048)
                    text = text + " 2048";

                if (info.getStatus() == PlayerInfo.STATUS_ACTIVE)
                    text = text + " Active";

                otherPlayersLabels.get(i).setText(text);
                otherPlayersLabels.get(i).setIcon(ImageCache.getPlayerIcon(p.getIconID()));
                return;
            }
            ++i;
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
