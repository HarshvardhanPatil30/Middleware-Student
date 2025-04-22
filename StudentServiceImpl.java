import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentServiceImpl extends UnicastRemoteObject implements StudentService {
    private static final long serialVersionUID = 1L;
    private Map<Integer, Student> students;
    
    public StudentServiceImpl() throws RemoteException {
        super();
        students = new HashMap<>();
        
        // Add some sample data
        addStudent(new Student(1, "John Doe", 20, "Computer Science", 3.8));
        addStudent(new Student(2, "Jane Smith", 21, "Mathematics", 3.9));
        addStudent(new Student(3, "Bob Johnson", 19, "Physics", 3.5));
    }

    @Override
    public void addStudent(Student student) throws RemoteException {
        students.put(student.getId(), student);
    }

    @Override
    public void updateStudent(Student student) throws RemoteException {
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
        }
    }

    @Override
    public void deleteStudent(int id) throws RemoteException {
        students.remove(id);
    }

    @Override
    public Student getStudent(int id) throws RemoteException {
        return students.get(id);
    }

    @Override
    public List<Student> getAllStudents() throws RemoteException {
        return new ArrayList<>(students.values());
    }
}