package sorny.application.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sorny.domain.prediction.CameTrueStatus;
import sorny.domain.prediction.PredictionEntity;
import sorny.api.PredictionFormBean;
import sorny.domain.prediction.PredictionRepository;
import sorny.domain.user.UserEntity;
import sorny.domain.user.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Created by Magnus on 2016-12-04.
 */
@Controller
public class PredictionController {
    @Autowired
    UserService userService;
    @Autowired
    PredictionRepository predictionRepository;

    @GetMapping("/predictions")
    public String predictionList(@RequestParam(value = "username", required = false) String username, Model model) {
        UserEntity user;
        if (!StringUtils.isEmpty(username))
            user = userService.getByUsername(username);
        else
            user = userService.getCurrentlyLoggedInUser();

        if (user == null)
            throw new IllegalArgumentException("No user found with username " + username);

        model.addAttribute("user", user);
        model.addAttribute("predictions", user.getPredictionEntities());

        return "predictionlist";
    }

    @GetMapping("/prediction")
    public String prediction(@RequestParam(value = "id", required = false) Long id, Model model) {
        UserEntity user = userService.getCurrentlyLoggedInUser();
        PredictionEntity predictionEntity = null;
        if (id != null) {
            predictionEntity = user.getPredictionWithId(id);
        }
        PredictionFormBean formBean;
        if (predictionEntity != null) {
            formBean = predictionEntity.asForm();
        } else {
            formBean = new PredictionFormBean();
            formBean.setId(id);
        }
        model.addAttribute("predictionForm", formBean);
        model.addAttribute("user", user);
        return "prediction";
    }

    @PostMapping("/prediction") // todo: add @Valid and validation
    public String predictionSave(@Valid @ModelAttribute PredictionFormBean formBean, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("predictionForm", formBean);
            UserEntity user = userService.getCurrentlyLoggedInUser();
            model.addAttribute("user", user);
            model.addAttribute("feedback", "Prediction has validation errors.");
            model.addAttribute("error", true);

            return "prediction";
        } else {
            PredictionFormBean result = null;
            try {
                result = userService.savePredictionForm(formBean);
            } catch (IllegalArgumentException e) {
                model.addAttribute("predictionForm", formBean);
                UserEntity user = userService.getCurrentlyLoggedInUser();
                model.addAttribute("user", user);
                model.addAttribute("feedback", "Prediction has validation errors. Fill out required fields and ensure unravelDate is later than tomorrow.");
                model.addAttribute("error", true);

                return "prediction";
            }
            model.addAttribute("predictionForm", result);
            UserEntity user = userService.getCurrentlyLoggedInUser();
            model.addAttribute("user", user);
            model.addAttribute("feedback", "Prediction created.");
            return "predictionOk";
        }
    }

    @GetMapping("/unravel")
    public String unravelPrediction(@RequestParam(value = "id", required = true) Long id,
                                    @RequestParam(value="cameTrue", required = true) String cameTrue, Model model) {
        CameTrueStatus cameTrueStatus = CameTrueStatus.valueOf(cameTrue);
        UserEntity user = userService.getCurrentlyLoggedInUser();
        PredictionEntity predictionEntity = userService.unravelPrediction(id, cameTrueStatus);
        if (predictionEntity == null)
            throw new IllegalArgumentException("No prediction with id " + id);

        model.addAttribute("user", user);
        model.addAttribute("prediction", predictionEntity);
        model.addAttribute("feedback", "Prediction decrypted, and status set to " + cameTrueStatus + ". Now, decide if/how you want to brag.");

        return "unravel";
    }

    @GetMapping("/brag") // todo: add possibility to make a brag comment
    public String brag(@RequestParam(value = "id", required = true) Long id, Model model) {
        UserEntity user = userService.getCurrentlyLoggedInUser();
        PredictionEntity predictionEntity = user.getPredictionWithId(id);
        if (predictionEntity == null)
            throw new IllegalArgumentException("No prediction with id " + id);

        model.addAttribute("user", user);
        model.addAttribute("prediction", predictionEntity);
        return "brag";
    }

    @GetMapping("/public-predicktion")
    public String publicPredictionShow(@RequestParam(value = "id", required = true) Long id, Model model) {
        PredictionEntity predictionEntity = predictionRepository.findOne(id);
        if (predictionEntity == null)
            throw new IllegalArgumentException("No prediction with id " + id);

        model.addAttribute("user", predictionEntity.getUser());
        model.addAttribute("prediction", predictionEntity);
        return "public-predicktion";
    }
}