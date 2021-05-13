package com.greally2014.ticketmanager.controller;

import com.greally2014.ticketmanager.dto.user.ResetPasswordDto;
import com.greally2014.ticketmanager.entity.User;
import com.greally2014.ticketmanager.exception.UserNotFoundException;
import com.greally2014.ticketmanager.service.CustomUserDetailsService;
import com.greally2014.ticketmanager.utility.Util;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@Controller
public class LoginController {

    /**
     * strips whitespace from form entries, converting them to null entries if pure whitespace.
     * pure whitespace entries are then flagged by the @NotNull annotation
     */
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    private final JavaMailSender mailSender;

    private final CustomUserDetailsService customUserDetailsService;

    public LoginController(JavaMailSender mailSender, CustomUserDetailsService customUserDetailsService) {
        this.mailSender = mailSender;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/")
    public String showRoleHomePage(Authentication authentication) {
        return "redirect:" + Util.determineTargetUrl(authentication); // redirects to url based on user role
    }

    @GetMapping("/login")
    public String showLoginForm(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login/login"; // if user is not authenticated, show login form
        }
        return "redirect:/"; // if user is already logged in, redirect to default url page
    }

    @GetMapping("/forgotPassword")
    public String showForgotPasswordForm() {
        return "login/forgot-password";
    }

    @PostMapping("/forgotPassword")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String code = RandomString.make(64); // unique email verification code

        try {
            customUserDetailsService.updateResetPasswordCode(code, email); // updates reset password code if email exists
            String resetPasswordLink = getSiteURL(request) + "/resetPassword?code=" + code;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (UserNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }

        return "login/forgot-password";
    }

    @GetMapping("/resetPassword")
    public String showResetPasswordForm(@Param(value = "code") String code, Model model) {
        User user = customUserDetailsService.getUserByResetPasswordCode(code);
        ResetPasswordDto passwordDto = new ResetPasswordDto(code); // contains password and matching password fields
        model.addAttribute("resetPasswordDto", passwordDto);

        if (user == null) {
            model.addAttribute("message", "Invalid code. Please re-enter email and try again.");
            return "login/forgot-password";
        }

        return "login/reset-password";
    }

    @PostMapping("/resetPassword")
    public String processResetPassword(@Valid @ModelAttribute("resetPasswordDto") ResetPasswordDto passwordDto,
                                       BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("resetPasswordDto", passwordDto);
            return "login/reset-password";
        }

        String code = passwordDto.getCode();
        String password = passwordDto.getPassword();

        User user = customUserDetailsService.getUserByResetPasswordCode(code);

        if (user == null) {
            model.addAttribute("message", "Invalid code. Please re-enter your email and try again.");
            return "login/forgot-password";
        } else {
            customUserDetailsService.updatePassword(user, password);
            return "login/reset-password-success";
        }
    }

    public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("greally2014@gmail.com", "Ticket Manager Company"); //host email address
        helper.setTo(recipientEmail);

        String subject = "Reset your password using this link";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to reset your password:</p>"
                + "<p><a href=\"" + link + "\">Reset password</a></p>" // contains user unique reset password code appended to context url
                + "<br>"
                + "<p>Ignore this email if do not want to reset your password, "
                + "or you do not remember making this request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
