package sorny.application.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import sorny.domain.MainApplicationService;

import javax.validation.Valid;

/**
 * Controller for prediction-related operations
 */
@Controller
public class PredictionController {
    @Autowired
    MainApplicationService mainApplicationService;
    @Autowired
    PredictionRepository predictionRepository;

    @GetMapping("/predictions")
    public String predictionList(@RequestParam(value = "username", required = false) String username, Model model) {
        UserEntity user;
        if (!StringUtils.isEmpty(username))
            user = mainApplicationService.getByUsername(username);
        else
            user = mainApplicationService.getCurrentlyLoggedInUser();

        if (user == null)
            throw new IllegalArgumentException("No user found with username " + username);

        model.addAttribute("user", user);
        model.addAttribute("predictions", user.getPredictionEntities());

        return "predictionlist";
    }

    @GetMapping("/delete")
    public String deletePrediction(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (mainApplicationService.deletePrediction(id)) {
            model.addAttribute("feedback", "Prediction deleted (you chicken).");
            model.addAttribute("error", null);
        } else {
            model.addAttribute("feedback", "Prediction could not be deleted. HA!");
            model.addAttribute("error", true);
        }

        UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
        model.addAttribute("user", user);
        model.addAttribute("predictions", user.getPredictionEntities());

        return "predictionlist";
    }

    @GetMapping("/prediction")
    public String prediction(@RequestParam(value = "id", required = false) Long id, Model model) {
        UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
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
            UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
            model.addAttribute("user", user);
            model.addAttribute("feedback", "Prediction has validation errors.");
            model.addAttribute("error", true);

            return "prediction";
        } else {
            PredictionFormBean result = null;
            try {
                result = mainApplicationService.savePredictionForm(formBean);
            } catch (IllegalArgumentException e) {
                model.addAttribute("predictionForm", formBean);
                UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
                model.addAttribute("user", user);
                model.addAttribute("feedback", "Prediction has validation errors. " +
                        "Fill out required fields and ensure unravelDate is later than tomorrow.");
                model.addAttribute("error", true);

                return "prediction";
            }
            model.addAttribute("predictionForm", result);
            UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
            model.addAttribute("user", user);
            model.addAttribute("feedback", "Prediction created.");
            return "predictionOk";
        }
    }

    @GetMapping("/unravel")
    public String unravelPrediction(@RequestParam(value = "id", required = true) Long id,
                                    @RequestParam(value="cameTrue", required = true) String cameTrue, Model model) {
        CameTrueStatus cameTrueStatus = CameTrueStatus.valueOf(cameTrue);
        UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
        PredictionEntity predictionEntity = null;
        try {
            predictionEntity = mainApplicationService.unravelPrediction(id, cameTrueStatus);
            model.addAttribute("error", null);
            model.addAttribute("user", user);
            model.addAttribute("prediction", predictionEntity);
            model.addAttribute("feedback", "Prediction decrypted, and status set to " + cameTrueStatus +
                    ". Now, decide if/how you want to announce it.");
        } catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("prediction", predictionEntity);
            model.addAttribute("feedback", "Couldn't unravel prediction: " + e.getMessage());
            model.addAttribute("error", true);
        }
        return "unravel";
    }

    @GetMapping("/announce") // todo: add possibility to make a brag comment
    public String announce(@RequestParam(value = "id", required = true) Long id, Model model) {
        UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
        PredictionEntity predictionEntity = user.getPredictionWithId(id);
        if (predictionEntity == null)
            throw new IllegalArgumentException("No prediction with id " + id);

        model.addAttribute("user", user);
        model.addAttribute("prediction", predictionEntity);
        return "announce";
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