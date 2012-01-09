/*
    This work is licensed under the GNU Public License (GPL).
    To view a copy of this license, visit http://www.gnu.org/copyleft/gpl.html

    Written by Abd Allah Diab (mpcabd)
    Email: mpcabd ^at^ gmail ^dot^ com
    Website: http://mpcabd.igeex.biz
 */

package statuschanger;

import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        mainFrame mf = new mainFrame();
        mf.setVisible(true);
    }
}
