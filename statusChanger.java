/*
This work is licensed under the GNU Public License (GPL).
To view a copy of this license, visit http://www.gnu.org/copyleft/gpl.html

Written by Abd Allah Diab (mpcabd)
Email: mpcabd ^at^ gmail ^dot^ com
Website: http://mpcabd.igeex.biz
 */
package statuschanger;

import java.util.Calendar;
import java.util.Date;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.proxy.ProxyInfo;

public class statusChanger {

    private XMPPConnection connection;
    private String username;
    private String password;
    private String server;
    private String lastMessage;
    private boolean isGmail;
    public boolean isInvisible;
    public Date endLine;
    public String message;
    public String postMessage;
    public Presence.Mode presenceMode;
    public ProxyInfo proxyInfo;

    private void sendPresence(String message) {
        message = "Testing ...";
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus(message);
        presence.setPriority(24);
        presence.setMode(presenceMode);
        connection.sendPacket(presence);
        final String finalMessage = message;
        if (isGmail) {
            connection.sendPacket(new IQ() {

                @Override
                public String getChildElementXML() {
                    return "<query xmlns='google:shared-status' version='2'><show>" + presenceMode + "</show><status>" + finalMessage + "</status><invisible value='" + (isInvisible ? "true" : "false") + "'/></query>";
                }

                @Override
                public String getTo() {
                    return username;
                }

                @Override
                public Type getType() {
                    return Type.SET;
                }
            });
        }
    }

    public statusChanger(String username, String password, String server) {
        this.username = username;
        this.password = password;
        this.server = server;
    }

    public void connect() throws Exception {
        ConnectionConfiguration cc;
        if (proxyInfo != null) {
            cc = new ConnectionConfiguration(server, proxyInfo);
        } else {
            cc = new ConnectionConfiguration(server);
        }
        connection = new XMPPConnection(cc);
        connection.connect();
        connection.login(username, password);

        if (server.toLowerCase().equals("gmail.com")) {
            isGmail = true;
        } else {
            isGmail = false;
        }

        if (isGmail) {
            connection.sendPacket(new IQ() {

                @Override
                public String getChildElementXML() {
                    return "<query xmlns='http://jabber.org/protocol/disco#info' />";
                }

                @Override
                public String getTo() {
                    return server;
                }

                @Override
                public Type getType() {
                    return Type.GET;
                }
            });



            connection.sendPacket(new IQ() {

                @Override
                public String getChildElementXML() {
                    return "<query xmlns='google:shared-status' version='2'/>";
                }

                @Override
                public String getTo() {
                    return username;
                }

                @Override
                public Type getType() {
                    return Type.GET;
                }
            });
        }
    }

    public void refreshPresence() {
        sendPresence(lastMessage);
    }

    public void updatePresence() {
        Calendar calendar1 = Calendar.getInstance();
        Date d = new Date();
        calendar1.setTime(d);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(endLine);

        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        String remaining = "";
        if (diff > 0) {
            long diffDays = diff / (24 * 60 * 60 * 1000);
            diff = diff % (24 * 60 * 60 * 1000);

            long diffHours = diff / (60 * 60 * 1000);
            diff = diff % (60 * 60 * 1000);

            long diffMinutes = diff / (60 * 1000);
            diff = diff % (60 * 1000);

            if (diffDays > 0) {
                remaining += Long.toString(diffDays) + " day" + (diffDays > 1 ? "s" : "");
            }

            if (diffHours > 0) {
                remaining += (remaining.equals("") ? "" : ", ") + Long.toString(diffHours) + " hour" + (diffHours > 1 ? "s" : "");
            }

            if (diffMinutes > 0) {
                remaining += (remaining.equals("") ? "" : ", ") + Long.toString(diffMinutes) + " minute" + (diffMinutes > 1 ? "s" : "") + " ";
            }

            if (remaining.equals("")) {
                remaining = postMessage;
            } else {
                remaining += message;
            }
        } else {
            remaining = postMessage;
        }
        lastMessage = remaining;
        refreshPresence();
    }
}
