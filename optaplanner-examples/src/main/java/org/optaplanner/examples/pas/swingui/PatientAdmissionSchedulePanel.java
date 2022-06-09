package org.optaplanner.examples.pas.swingui;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN_GROUP1;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN_GROUP2;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.HEADER_ROW;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.pas.domain.AdmissionPart;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.Department;
import org.optaplanner.examples.pas.domain.Gender;
import org.optaplanner.examples.pas.domain.GenderLimitation;
import org.optaplanner.examples.pas.domain.Night;
import org.optaplanner.examples.pas.domain.Patient;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.domain.RequiredPatientEquipment;
import org.optaplanner.examples.pas.domain.Room;
import org.optaplanner.examples.pas.domain.RoomEquipment;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class PatientAdmissionSchedulePanel extends SolutionPanel<PatientAdmissionSchedule> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/pas/swingui/pasLogo.png";

    private final ImageIcon anyGenderIcon;
    private final ImageIcon maleIcon;
    private final ImageIcon femaleIcon;
    private final ImageIcon sameGenderIcon;

    private TimeTablePanel<Night, Bed> timeTablePanel;
    private TangoColorFactory equipmentTangoColorFactory;

    public PatientAdmissionSchedulePanel() {
        anyGenderIcon = new ImageIcon(getClass().getResource("anyGender.png"));
        maleIcon = new ImageIcon(getClass().getResource("male.png"));
        femaleIcon = new ImageIcon(getClass().getResource("female.png"));
        sameGenderIcon = new ImageIcon(getClass().getResource("sameGender.png"));
        setLayout(new BorderLayout());
        timeTablePanel = new TimeTablePanel<>();
        add(timeTablePanel, BorderLayout.CENTER);
    }

    @Override
    public void resetPanel(PatientAdmissionSchedule patientAdmissionSchedule) {
        timeTablePanel.reset();
        equipmentTangoColorFactory = new TangoColorFactory();
        defineGrid(patientAdmissionSchedule);
        fillCells(patientAdmissionSchedule);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(PatientAdmissionSchedule patientAdmissionSchedule) {
        JButton footprint = SwingUtils.makeSmallButton(new JButton("Patient9999"));
        int footprintWidth = footprint.getPreferredSize().width;
        timeTablePanel.defineColumnHeaderByKey(HEADER_COLUMN_GROUP2); // Department Header
        timeTablePanel.defineColumnHeaderByKey(HEADER_COLUMN_GROUP1); // Room Header
        timeTablePanel.defineColumnHeaderByKey(HEADER_COLUMN); // Bed Header
        for (Night night : patientAdmissionSchedule.getNightList()) {
            timeTablePanel.defineColumnHeader(night, footprintWidth);
        }
        timeTablePanel.defineRowHeaderByKey(HEADER_ROW); // Night header
        timeTablePanel.defineRowHeader(null); // Unassigned bed
        for (Bed bed : patientAdmissionSchedule.getBedList()) {
            timeTablePanel.defineRowHeader(bed);
        }
    }

    private void fillCells(PatientAdmissionSchedule patientAdmissionSchedule) {
        timeTablePanel.addCornerHeader(HEADER_COLUMN_GROUP2, HEADER_ROW, createHeaderPanel(new JLabel("Department")));
        timeTablePanel.addCornerHeader(HEADER_COLUMN_GROUP1, HEADER_ROW, createHeaderPanel(new JLabel("Room")));
        timeTablePanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createHeaderPanel(new JLabel("Bed")));
        fillNightCells(patientAdmissionSchedule);
        fillBedCells(patientAdmissionSchedule);
        fillBedDesignationCells(patientAdmissionSchedule);
    }

    private void fillNightCells(PatientAdmissionSchedule patientAdmissionSchedule) {
        for (Night night : patientAdmissionSchedule.getNightList()) {
            timeTablePanel.addColumnHeader(night, HEADER_ROW,
                    createHeaderPanel(new JLabel(night.getLabel(), SwingConstants.CENTER)));
        }
    }

    private void fillBedCells(PatientAdmissionSchedule patientAdmissionSchedule) {
        timeTablePanel.addRowHeader(HEADER_COLUMN_GROUP2, null, HEADER_COLUMN, null,
                createHeaderPanel(new JLabel("Unassigned")));
        for (Department department : patientAdmissionSchedule.getDepartmentList()) {
            List<Room> roomList = department.getRoomList();
            List<Bed> firstRoomBedList = roomList.get(0).getBedList();
            List<Bed> lastRoomBedList = roomList.get(roomList.size() - 1).getBedList();
            timeTablePanel.addRowHeader(HEADER_COLUMN_GROUP2, firstRoomBedList.get(0),
                    HEADER_COLUMN_GROUP2, lastRoomBedList.get(lastRoomBedList.size() - 1),
                    createHeaderPanel(new JLabel(department.getLabel())));
            for (Room room : roomList) {
                List<Bed> bedList = room.getBedList();
                JLabel roomLabel = new JLabel(room.getLabel(), new PatientOrRoomIcon(room), SwingConstants.RIGHT);
                timeTablePanel.addRowHeader(HEADER_COLUMN_GROUP1, bedList.get(0),
                        HEADER_COLUMN_GROUP1, bedList.get(bedList.size() - 1),
                        createHeaderPanel(roomLabel));
                for (Bed bed : bedList) {
                    timeTablePanel.addRowHeader(HEADER_COLUMN, bed,
                            createHeaderPanel(new JLabel(bed.getLabelInRoom(), SwingConstants.RIGHT)));
                }
            }
        }
    }

    private void fillBedDesignationCells(PatientAdmissionSchedule patientAdmissionSchedule) {
        for (BedDesignation bedDesignation : patientAdmissionSchedule.getBedDesignationList()) {
            JButton button = SwingUtils.makeSmallButton(new JButton(new BedDesignationAction(bedDesignation)));
            AdmissionPart admissionPart = bedDesignation.getAdmissionPart();
            timeTablePanel.addCell(admissionPart.getFirstNight(), bedDesignation.getBed(),
                    admissionPart.getLastNight(), bedDesignation.getBed(), button);
        }
    }

    private JPanel createHeaderPanel(JLabel label) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

    private class BedDesignationAction extends AbstractAction {

        private final BedDesignation bedDesignation;

        public BedDesignationAction(BedDesignation bedDesignation) {
            super(bedDesignation.getAdmissionPart().getPatient().getName(),
                    new PatientOrRoomIcon(bedDesignation.getAdmissionPart().getPatient()));
            this.bedDesignation = bedDesignation;
            Patient patient = bedDesignation.getPatient();
            // Tooltip
            putValue(SHORT_DESCRIPTION, "<html>Patient: " + patient.getName() + "<br/>"
                    + "Gender: " + patient.getGender().getCode() + " (see icon)<br/>"
                    + "Age: " + patient.getAge() + "<br/>"
                    + "Preferred maximum room capacity: " + patient.getPreferredMaximumRoomCapacity() + "<br/>"
                    + "Requires " + patient.getRequiredPatientEquipmentList().size() + " equipments (shown as rectangles)<br/>"
                    + "Prefers " + patient.getPreferredPatientEquipmentList().size() + " equipments"
                    + "</html>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 1));
            List<Bed> bedList = getSolution().getBedList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox bedListField = new JComboBox(
                    bedList.toArray(new Object[bedList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(bedListField);
            bedListField.setSelectedItem(bedDesignation.getBed());
            listFieldsPanel.add(bedListField);
            int result = JOptionPane.showConfirmDialog(PatientAdmissionSchedulePanel.this.getRootPane(),
                    listFieldsPanel, "Select bed for " + bedDesignation.getAdmissionPart().getPatient().getName(),
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Bed toBed = (Bed) bedListField.getSelectedItem();
                doProblemChange((workingSolution, problemChangeDirector) -> problemChangeDirector.changeVariable(bedDesignation,
                        "bed", bd -> bd.setBed(toBed)));
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

    private class PatientOrRoomIcon implements Icon {

        private static final int EQUIPMENT_ICON_WIDTH = 8;

        private final ImageIcon genderIcon;
        private final List<Color> equipmentColorList;

        private PatientOrRoomIcon(Patient patient) {
            genderIcon = determinePatientGenderIcon(patient.getGender());
            List<RequiredPatientEquipment> equipmentList = patient.getRequiredPatientEquipmentList();
            equipmentColorList = new ArrayList<>(equipmentList.size());
            for (RequiredPatientEquipment equipment : equipmentList) {
                equipmentColorList.add(equipmentTangoColorFactory.pickColor(equipment.getEquipment()));
            }
        }

        private PatientOrRoomIcon(Room room) {
            genderIcon = determineRoomGenderIcon(room.getGenderLimitation());
            List<RoomEquipment> equipmentList = room.getRoomEquipmentList();
            equipmentColorList = new ArrayList<>(equipmentList.size());
            for (RoomEquipment equipment : equipmentList) {
                equipmentColorList.add(equipmentTangoColorFactory.pickColor(equipment.getEquipment()));
            }
        }

        @Override
        public int getIconWidth() {
            return genderIcon.getIconWidth() + equipmentColorList.size() * EQUIPMENT_ICON_WIDTH;
        }

        @Override
        public int getIconHeight() {
            return genderIcon.getIconHeight();
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            genderIcon.paintIcon(c, g, x, y);
            int innerX = x + genderIcon.getIconWidth();
            int equipmentIconHeight = genderIcon.getIconHeight();
            for (int i = 0; i < equipmentColorList.size(); i++) {
                g.setColor(equipmentColorList.get(i));
                g.fillRect(innerX + 1, y + 1, EQUIPMENT_ICON_WIDTH - 2, equipmentIconHeight - 2);
                g.setColor(TangoColorFactory.ALUMINIUM_5);
                g.drawRect(innerX + 1, y + 1, EQUIPMENT_ICON_WIDTH - 2, equipmentIconHeight - 2);
                innerX += EQUIPMENT_ICON_WIDTH;
            }
        }

    }

    private ImageIcon determineRoomGenderIcon(GenderLimitation genderLimitation) {
        switch (genderLimitation) {
            case ANY_GENDER:
                return anyGenderIcon;
            case MALE_ONLY:
                return maleIcon;
            case FEMALE_ONLY:
                return femaleIcon;
            case SAME_GENDER:
                return sameGenderIcon;
            default:
                throw new IllegalStateException("The genderLimitation (" + genderLimitation + ") is not implemented.");
        }
    }

    private ImageIcon determinePatientGenderIcon(Gender gender) {
        switch (gender) {
            case MALE:
                return maleIcon;
            case FEMALE:
                return femaleIcon;
            default:
                throw new IllegalStateException("The gender (" + gender + ") is not implemented.");
        }
    }

}
