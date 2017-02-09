
package com.pj.client.file;

/**
 *
 * @author pengju
 */
public interface Parameter {
    public String   getParameter(String name);
    public String[] getParameterValues(String name);
    public String[] getParameterNames();
}
