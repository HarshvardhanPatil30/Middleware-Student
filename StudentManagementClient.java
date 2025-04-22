import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class StudentManagementClient extends JFrame {
    private StudentService studentService;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, ageField, courseField, gradeField;
    private JButton addButton, updateButton, deleteButton, clearButton, refreshButton;

    public StudentManagementClient() throws Exception {
        super("Student Management System");
        
        // Connect to the RMI server
        connectToServer();
        
        // Set up the UI
        setupUI();
        
        // Load initial data
        loadStudentData();
        
        // Configure window
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void connectToServer() throws Exception {
        try {
            // Get the registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            // Look up the remote object
            studentService = (StudentService) registry.lookup("StudentService");
            
            System.out.println("Connected to the Student Management Server");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to server. Please make sure the server is running.",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }
    
    private void setupUI() {
        // Create the table model with column names
        String[] columnNames = {"ID", "Name", "Age", "Course", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        // Create the table with the model
        studentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        
        // Create input panel
        JPanel inputPanel = createInputPanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to the content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(tableScrollPane, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        contentPane.add(southPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        
        panel.add(new JLabel("ID:"));
        idField = new JTextField();
        panel.add(idField);
        
        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);
        
        panel.add(new JLabel("Age:"));
        ageField = new JTextField();
        panel.add(ageField);
        
        panel.add(new JLabel("Course:"));
        courseField = new JTextField();
        panel.add(courseField);
        
        panel.add(new JLabel("Grade:"));
        gradeField = new JTextField();
        panel.add(gradeField);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        refreshButton = new JButton("Refresh");
        
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(refreshButton);
        
        // Add action listeners
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadStudentData());
        
        // Add table selection listener
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() != -1) {
                int selectedRow = studentTable.getSelectedRow();
                idField.setText(studentTable.getValueAt(selectedRow, 0).toString());
                nameField.setText(studentTable.getValueAt(selectedRow, 1).toString());
                ageField.setText(studentTable.getValueAt(selectedRow, 2).toString());
                courseField.setText(studentTable.getValueAt(selectedRow, 3).toString());
                gradeField.setText(studentTable.getValueAt(selectedRow, 4).toString());
            }
        });
        
        return panel;
    }
    
    private void loadStudentData() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Get all students from the server
            List<Student> students = studentService.getAllStudents();
            
            // Add each student to the table
            for (Student student : students) {
                Object[] row = {
                    student.getId(),
                    student.getName(),
                    student.getAge(),
                    student.getCourse(),
                    student.getGrade()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading student data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addStudent() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }
            
            // Create a new student from the form data
            Student student = getStudentFromForm();
            
            // Add the student to the server
            studentService.addStudent(student);
            
            // Reload the table data
            loadStudentData();
            
            // Clear the form fields
            clearFields();
            
            JOptionPane.showMessageDialog(this, 
                "Student added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error adding student: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStudent() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }
            
            // Create a student from the form data
            Student student = getStudentFromForm();
            
            // Update the student on the server
            studentService.updateStudent(student);
            
            // Reload the table data
            loadStudentData();
            
            JOptionPane.showMessageDialog(this, 
                "Student updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error updating student: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteStudent() {
        try {
            // Validate ID field
            if (idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a student ID to delete.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int id = Integer.parseInt(idField.getText());
            
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the student with ID " + id + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Delete the student on the server
                studentService.deleteStudent(id);
                
                // Reload the table data
                loadStudentData();
                
                // Clear the form fields
                clearFields();
                
                JOptionPane.showMessageDialog(this, 
                    "Student deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error deleting student: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        courseField.setText("");
        gradeField.setText("");
        studentTable.clearSelection();
    }
    
    private boolean validateInput() {
        // Check if required fields are empty
        if (idField.getText().trim().isEmpty() ||
            nameField.getText().trim().isEmpty() ||
            ageField.getText().trim().isEmpty() ||
            courseField.getText().trim().isEmpty() ||
            gradeField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "All fields are required.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validate ID
        try {
            Integer.parseInt(idField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "ID must be a valid integer.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validate Age
        try {
            int age = Integer.parseInt(ageField.getText());
            if (age < 0 || age > 120) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Age must be a valid integer between 0 and 120.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validate Grade
        try {
            double grade = Double.parseDouble(gradeField.getText());
            if (grade < 0 || grade > 4.0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Grade must be a valid number between 0.0 and 4.0.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private Student getStudentFromForm() {
        int id = Integer.parseInt(idField.getText().trim());
        String name = nameField.getText().trim();
        int age = Integer.parseInt(ageField.getText().trim());
        String course = courseField.getText().trim();
        double grade = Double.parseDouble(gradeField.getText().trim());
        
        return new Student(id, name, age, course, grade);
    }
}