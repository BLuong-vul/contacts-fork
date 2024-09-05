package vision.webapp;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoRequest {

    @CrossOrigin(origins = {"http://127.0.0.1:3000"})
    @GetMapping("/request-data")
    public String request() {
        return "API Test";
    }
}

