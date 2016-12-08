package sorny.application.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import sorny.api.error.IllegalEmailAddressException;
import sorny.domain.user.UserEntity;
import sorny.api.UserDetailsFormBean;
import sorny.domain.MainApplicationService;

import javax.validation.Valid;

/**
 * Controller for user-related operations
 */
@Controller
public class UserController {
    @Autowired
    MainApplicationService mainApplicationService;

    @GetMapping("/signup")
    public String signup(@ModelAttribute UserDetailsFormBean formBean, BindingResult bindingResult, Model model) {
        model.addAttribute("formBean", formBean);
        model.addAttribute("error", null);
        model.addAttribute("feedback", null);
        return "signup";
    }

    @PostMapping("/signup")
    @Transactional
    public String userCreate(@Valid @ModelAttribute UserDetailsFormBean formBean, BindingResult bindingResult, Model model) {
        model.addAttribute("formBean", formBean);
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        String feedback = null;
        Boolean error = null;
        UserEntity user = mainApplicationService.signUp(formBean.getUsername(), formBean.getNewPassword(), formBean.getEmail());
        final String errorText = "User " + user.getUsername() + " not created due to an error.";

        if (user != null) {
            model.addAttribute("error", error);
            model.addAttribute("feedback", "User SignUp successful. Go ahead and login!");
            return "login";
        } else {
            feedback = errorText;
            error = true;
            model.addAttribute("error", error);
            model.addAttribute("feedback", feedback);
        }

        return "signup";
    }

    @GetMapping("/userdetails")
    public String userDetails(Model model) {
        UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
        final UserDetailsFormBean userDetailsFormBean = new UserDetailsFormBean(user.email, null, null);
        userDetailsFormBean.setUsername(user.username);
        model.addAttribute("user", user);
        model.addAttribute("userDetails", userDetailsFormBean);
        model.addAttribute("error", null);
        model.addAttribute("feedback", null);
        return "userdetails";
    }

    @PostMapping("/userdetails")
    @Transactional
    public String userDetailsUpdate(@ModelAttribute UserDetailsFormBean formBean, Model model) {
        UserEntity user = mainApplicationService.getCurrentlyLoggedInUser();
        final String errorText = "User " + user.getUsername() + " not updated, because you don't know what you're doing.";

        Boolean error = null;
        String feedback;
        String email = user.email;

        final boolean userHasFilledOutPasswordChangeFields = !StringUtils.isEmpty(formBean.getOldPassword()) && !StringUtils.isEmpty(formBean.getNewPassword());
        if (userHasFilledOutPasswordChangeFields) {
            final boolean passwordChangeIsNotApproved = !user.isPassword(formBean.getOldPassword()) || (!UserEntity.isPasswordApproved(formBean.getNewPassword()));
            if (passwordChangeIsNotApproved) {
                error = true;
            } else {
                user.setPassword(formBean.getNewPassword());
            }
        }

        if (error == null) {
            try {
                user.setEmail(formBean.getEmail());
                email = user.email;
            } catch (IllegalEmailAddressException e) {
                error = true;
            }
        }

        if (error == null) {
            user = mainApplicationService.persist(user);
            feedback = "User details updated." +
                    (!StringUtils.isEmpty(formBean.getNewPassword()) ? "" :
                            " Well, atleast your email since you're too dumb to handle the password change.");
        } else {
            feedback = errorText;
        }

        final UserDetailsFormBean newUserDetailsFormBean = new UserDetailsFormBean(email, null, null);
        newUserDetailsFormBean.setUsername(user.username);
        model.addAttribute("userDetails", newUserDetailsFormBean);
        model.addAttribute("feedback", feedback);
        model.addAttribute("error", error);
        model.addAttribute("user", user);

        return "userdetails";
    }
}
