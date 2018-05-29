/*
Copyright (c) 2008,  Felipe Lessa

Developed by Felipe Lessa (felipe.lessa@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the 
"Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, 
distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be 
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */
package mars.tools;

import javafx.scene.media.MediaPlayer;
import mars.Globals;
import mars.ProgramStatement;
import mars.mips.hardware.*;
import mars.mips.instructions.BasicInstruction;
import mars.mips.instructions.BasicInstructionFormat;
import mars.venus.NumberDisplayBaseChooser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Number Pyramids tool. Can be used to know how many instructions
 * were executed to complete a given program.
 * <p>
 * Code slightly based on MemoryReferenceVisualization.
 *
 * @author Felipe Lessa <felipe.lessa@gmail.com>
 */
//@SuppressWarnings("serial")
@SuppressWarnings("Duplicates")
public class NumberPyramids extends AbstractMarsToolAndApplication {
    private static String name = "Number Pyramids";
    private static String version = "Version 1.0 (Saran Sundararajan)";
    private static String heading = "A Graphical Representation for Number Pyramids";

    /**
     * Number of instructions executed until now.
     */
    protected int counter = 0;
    /**
     * Number of instructions of type R.
     */
    protected int counterR = 0;
    /**
     * Number of instructions of type I.
     */
    protected int counterI = 0;
    /**
     * Number of instructions of type J.
     */
    protected int counterJ = 0;
    /**
     * The last address we saw. We ignore it because the only way for a
     * program to execute twice the same instruction is to enter an infinite
     * loop, which is not insteresting in the POV of counting instructions.
     */
    protected int lastAddress = -1;
    private JTextField counterField;
    private JTextField counterRField;
    private JProgressBar progressbarR;
    private JTextField counterIField;
    private JProgressBar progressbarI;
    private JTextField counterJField;
    private JProgressBar progressbarJ;
    private ArrayList<JTextField> textFields;
    private MediaPlayer mediaPlayer;

    /**
     * Simple constructor, likely used to run a stand-alone memory reference visualizer.
     *
     * @param title   String containing title for title bar
     * @param heading String containing text for heading shown in upper part of window.
     */
    public NumberPyramids(String title, String heading) {
        super(title, heading);
    }

    /**
     * Simple construction, likely used by the MARS Tools menu mechanism.
     */
    public NumberPyramids() {
        super(name + ", " + version, heading);
    }

    //	@Override
    public String getName() {
        return name;
    }

    public JPanel makeScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        textFields = new ArrayList<>();
        int w = 50;
        int h = 50;
        int yOffset = 15;
        int xOffset = 65;
        double[] offsets = {3, 2.5, 2, 1.5, 1, 0.5, 0};
        String[] alph = {"A", "B", "C", "D", "E", "F", "G"};
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < r + 1; c++) {
                JTextField field = new JTextField(alph[r] + alph[c]);
                field.setBounds(xOffset + ((int) (offsets[r] * w) + (c * w)), yOffset + (r * h), w, h);
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setEnabled(false);
                field.setBackground(Color.orange);
                field.setSelectedTextColor(Color.red);
                field.setDisabledTextColor(Color.red);
                textFields.add(field);
                panel.add(field);
            }
        }
        panel.setBackground(Color.yellow);
        return panel;
    }

    //	@Override
    protected JComponent buildMainDisplayArea() {
        JPanel panel = new JPanel(new GridBagLayout());

        counterField = new JTextField("0", 10);
        counterField.setEditable(false);

        counterRField = new JTextField("0", 10);
        counterRField.setEditable(false);
        progressbarR = new JProgressBar(JProgressBar.HORIZONTAL);
        progressbarR.setStringPainted(true);

        counterIField = new JTextField("0", 10);
        counterIField.setEditable(false);
        progressbarI = new JProgressBar(JProgressBar.HORIZONTAL);
        progressbarI.setStringPainted(true);

        counterJField = new JTextField("0", 10);
        counterJField.setEditable(false);
        progressbarJ = new JProgressBar(JProgressBar.HORIZONTAL);
        progressbarJ.setStringPainted(true);

        // Add them to the panel

        // Fields
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridheight = c.gridwidth = 1;
        c.gridx = 3;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 17, 0);
        panel.add(counterField, c);

        c.insets = new Insets(0, 0, 0, 0);
        c.gridy++;
        panel.add(counterRField, c);

        c.gridy++;
        panel.add(counterIField, c);

        c.gridy++;
        panel.add(counterJField, c);

        // Labels
        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        c.gridwidth = 2;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 17, 0);
        panel.add(new JLabel("Instructions so far: "), c);

        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 2;
        c.gridwidth = 1;
        c.gridy++;
        panel.add(new JLabel("R-type: "), c);

        c.gridy++;
        panel.add(new JLabel("I-type: "), c);

        c.gridy++;
        panel.add(new JLabel("J-type: "), c);

        // Progress bars
        c.insets = new Insets(3, 3, 3, 3);
        c.gridx = 4;
        c.gridy = 2;
        panel.add(progressbarR, c);

        c.gridy++;
        panel.add(progressbarI, c);

        c.gridy++;
        panel.add(progressbarJ, c);

        panel = makeScreen();
        return panel;
    }

    //	@Override
    protected void addAsObserver() {
        addAsObserver(Memory.textBaseAddress, Memory.textLimitAddress);
    }

    //	@Override
    protected void processMIPSUpdate(Observable resource, AccessNotice notice) {
        if (!notice.accessIsFromMIPS()) return;
        if (notice.getAccessType() != AccessNotice.READ) return;
        MemoryAccessNotice m = (MemoryAccessNotice) notice;
        int a = m.getAddress();
        if (a == lastAddress) return;
        lastAddress = a;
        counter++;
        try {
            ProgramStatement stmt = Memory.getInstance().getStatement(a);
            BasicInstruction instr = (BasicInstruction) stmt.getInstruction();
            BasicInstructionFormat format = instr.getInstructionFormat();
            String command = stmt.getSource();
            if (command.contains("@DRAW")) {
                int valueBase = NumberDisplayBaseChooser.getBase(Globals.getSettings().getDisplayValuesInHex());
                System.out.println(command);
                Register[] registers = RegisterFile.getRegisters();
                String registerAt = registers[1].getName();
                String registerA0 = registers[4].getName();
                System.out.print(registerAt + " ");
                String a1 = NumberDisplayBaseChooser.formatNumber(registers[5].getValue(), valueBase);
                int a1I = Integer.decode(a1);
                System.out.println(a1I);
                System.out.print(registerA0 + " ");
                String a0 = NumberDisplayBaseChooser.formatNumber(registers[4].getValue(), valueBase);
                int a0I = Integer.decode(a0);
                System.out.println(a0I);
                textFields.get(a1I / 4).setText("" + a0I);
            } else if (command.contains("@MUSIC-START")) {
                setupMusicPlayer();
            } else if (command.contains("@MUSIC-STOP")) {
                Sound.sound2.stop();
            }
//            if (command.contains("DRAW")) {
//                int valueBase = NumberDisplayBaseChooser.getBase(Globals.getSettings().getDisplayValuesInHex());
//                System.out.println(command);
//                for (int i = 0; i < registers.length; i++) {
//                    if (registers[i].getName().contains("a")) {
//                        System.out.print(registers[i].getName() + " ");
//                        System.out.println(NumberDisplayBaseChooser.formatNumber(registers[i].getValue(), valueBase));
//                        System.out.println("######################");
//                        break;
//                    }
//                }
//            }
        } catch (AddressErrorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        updateDisplay();
    }

    public void setupMusicPlayer() {
        Sound.sound2.loop();
        Sound.sound2.setVolume(1);
    }

    //	@Override
    protected void initializePreGUI() {
        counter = counterR = counterI = counterJ = 0;
        lastAddress = -1;
    }

    // @Override
    protected void initializePostGUI() {
        theWindow.setMinimumSize(new Dimension(500, 500));
    }

    // @Override
    protected void reset() {
        counter = counterR = counterI = counterJ = 0;
        lastAddress = -1;
        updateDisplay();
    }

    //	@Override
    protected void updateDisplay() {
//        counterField.setText(String.valueOf(counter));
//
//        counterRField.setText(String.valueOf(counterR));
//        progressbarR.setMaximum(counter);
//        progressbarR.setValue(counterR);
//
//        counterIField.setText(String.valueOf(counterI));
//        progressbarI.setMaximum(counter);
//        progressbarI.setValue(counterI);
//
//        counterJField.setText(String.valueOf(counterJ));
//        progressbarJ.setMaximum(counter);
//        progressbarJ.setValue(counterJ);
//
//        if (counter == 0) {
//            progressbarR.setString("0%");
//            progressbarI.setString("0%");
//            progressbarJ.setString("0%");
//        } else {
//            progressbarR.setString((counterR * 100) / counter + "%");
//            progressbarI.setString((counterI * 100) / counter + "%");
//            progressbarJ.setString((counterJ * 100) / counter + "%");
//        }
    }
}
