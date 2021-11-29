package org.eclipse.jetty.issue;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoBodyServlet extends HttpServlet
{
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        // it is the responsibility of the server endpoint to always read the request, even if there is nothing there.
        InputStream in = req.getInputStream();

        int read;
        do
        {
            read = in.read();
        }
        while (read != -1);

        resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
    }
}
