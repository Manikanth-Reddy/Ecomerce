package com.ecom.util;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.impl.UserServiceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${aws.s3.bucket.category}")
	private String categoryBucket;

	@Value("${aws.s3.bucket.product}")
	private String productBucket;

	@Value("${aws.s3.bucket.profile}")
	private String profileBucket;

//	Send mail on Succesfull Registration

	public void sendMailOnSuccessfullRegistration(UserDtls user)
			throws UnsupportedEncodingException, MessagingException {

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		helper.setFrom("yourmail@gmail.com", "Ecommerce Site");
		helper.setTo(user.getEmail());
		String message = "<p>Hello,</p>" + user.getName()
				+ "<p> <b>You have successfully Registered to Ecommerce Site.</b></p>" + "<p><b>UserName : </b></p>"
				+ user.getName() + "<p><b>Password : </b></p>" + user.getOriginalPassword();
		helper.setSubject("Ecommerce Site Registration ");
		helper.setText(message, true);
		mailSender.send(mimeMessage);
	}
//Send mail on Password Update

	public void sendMailOnPasswordUpdate(UserDtls user, String password)
			throws UnsupportedEncodingException, MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		helper.setFrom("yourmail@gmail.com", "Ecommerce Site");
		helper.setTo(user.getEmail());
		String message = "<p>Hello,</p>" + user.getName()
				+ "<p> <b>You have successfully Updated Your  Ecommerce Site password .</b></p>"
				+ "<p><b>UserName : </b></p>" + user.getName() + "<p><b>Updated Password : </b></p>" + password;
		helper.setSubject("Ecommerce Site Password Updated ");
		helper.setText(message, true);
		mailSender.send(mimeMessage);
	}

	public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("yourmail@gmail.com", "Ecommerce Site");
		helper.setTo(reciepentEmail);

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>";
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {

		// http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();

		return siteUrl.replace(request.getServletPath(), "");
	}

	String msg = null;;

	public Boolean sendMailForProductOrder(ProductOrder order, String status) throws Exception {

		msg = "<p>Hello [[name]],</p>" + "<p>Thank you order <b>[[orderStatus]]</b>.</p>"
				+ "<p><b>Product Details:</b></p>" + "<p>Name : [[productName]]</p>" + "<p>Category : [[category]]</p>"
				+ "<p>Quantity : [[quantity]]</p>" + "<p>Price : [[price]]</p>"
				+ "<p>Payment Type : [[paymentType]]</p>";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("yourmail@gmail.com", "Shooping Cart");
		helper.setTo(order.getOrderAddress().getEmail());

		msg = msg.replace("[[name]]", order.getOrderAddress().getFirstName());
		msg = msg.replace("[[orderStatus]]", status);
		msg = msg.replace("[[productName]]", order.getProduct().getTitle());
		msg = msg.replace("[[category]]", order.getProduct().getCategory());
		msg = msg.replace("[[quantity]]", order.getQuantity().toString());
		msg = msg.replace("[[price]]", order.getPrice().toString());
		msg = msg.replace("[[paymentType]]", order.getPaymentType());

		helper.setSubject("Product Order Status");
		helper.setText(msg, true);
		mailSender.send(message);
		return true;
	}

	public UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserServiceImpl userService = new UserServiceImpl();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}

//image url from AWS
	public String getImageUrl(MultipartFile file, Integer bucketType) {

		String bucketName = null;
		if (bucketType == 1) {
			bucketName = categoryBucket;
		} else if (bucketType == 2) {
			bucketName = productBucket;
		} else
			bucketName = profileBucket;

//https://ecommerce-category.s3.eu-west-2.amazonaws.com/IMG-20220909-WA0000.jpg
		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";

		String imgUrl = "https://" + bucketName + ".s3.eu-west-2.amazonaws.com/" + imageName;

		return imgUrl;
	}
}
