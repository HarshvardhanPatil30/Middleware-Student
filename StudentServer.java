import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StudentServer {
    public static void main(String[] args) {
        try {
            // Create and export the registry instance on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Create service implementation
            StudentServiceImpl service = new StudentServiceImpl();
            
            // Bind the service to the registry
            registry.rebind("StudentService", service);
            
            System.out.println("Student Management Server is running...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}