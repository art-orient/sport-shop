package com.art.orion.tag;

import com.art.orion.util.ErrorMessageManager;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import java.io.IOException;

public class CustomTag extends TagSupport {

    @Override
    public int doStartTag() throws JspException {
        String author = ErrorMessageManager.getMessage("msg.author");
        String message =
            String.format("<p style=\"color: yellow\">%s</p><p style=\"color: yellow\">EPAM Systems</p>", author);
        try {
            JspWriter out = pageContext.getOut();
            out.write(message);
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }
}