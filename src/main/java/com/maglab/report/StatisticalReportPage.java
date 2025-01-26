package com.maglab.report;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import com.maglab.panel.MyHomePage;
import com.maglab.panel.ProbHomePage;

import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

public class StatisticalReportPage extends WebPage {
    private static final long serialVersionUID = 1L;
    private WebMarkupContainer reportContainer;
    public StatisticalReportPage() {
    	//super(id);
        // Add a placeholder for the report
        //Panel reportContainer = new EmptyPanel("reportContainer");
        reportContainer = new WebMarkupContainer("reportContainer");
        reportContainer.setOutputMarkupId(true);
        add(reportContainer);

        // Add the button to generate the report
       /* add(new Link<Void>("generateReportButton") {
            @Override
            public void onClick() {
                // Generate the report
                String report = generateReport();

                // Replace the container with the report content
                Fragment reportFragment = new Fragment("reportContainer", "reportFragment", StatisticalReportPage.this);
                reportFragment.add(new Label("reportContent", report).setEscapeModelStrings(false));
                reportContainer.replaceWith(reportFragment);
                reportContainer = reportFragment;
                setResponsePage(StatisticalReportPage.this);
                
            }
        });
        */
        BookmarkablePageLink pl = new BookmarkablePageLink("cancelLink", MyHomePage.class);
		add(pl);
        add(new AjaxLink<Void>("generateReportButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                // Generate the report
                String report = generateReport();

                // Replace the container with the report content
                Fragment reportFragment = new Fragment("reportContainer", "reportFragment", StatisticalReportPage.this);
                reportFragment.add(new Label("reportContent", report).setEscapeModelStrings(false));

                reportContainer.replaceWith(reportFragment);
                reportContainer = reportFragment;

                // Update the container dynamically via AJAX
                target.add(reportContainer);
            }
        });
    
    }

    
    
    private String generateReport() {
        StringBuilder reportBuilder = new StringBuilder();
        String sql = "SELECT strftime('%Y', dtgranted) AS year, "+
                " (CAST(strftime('%m', dtgranted) AS INTEGER) - 1) / 3 + 1 AS quarter, " +
                 

                " COUNT(DISTINCT  expnode) AS unique_count " +
            " FROM osf_user_access_log WHERE "+ 
                "osf_name IS NOT NULL AND expnode IS NOT NULL GROUP BY year, quarter "+
            " ORDER BY year, quarter;";
        
        String sql2 = "SELECT strftime('%Y', dtgranted) AS year, "+
                " (CAST(strftime('%m', dtgranted) AS INTEGER) - 1) / 3 + 1 AS quarter, " +
                 

                " COUNT(DISTINCT  osf_name) AS user_count " +
            " FROM osf_user_access_log WHERE "+ 
                "osf_name IS NOT NULL AND expnode IS NOT NULL GROUP BY year, quarter "+
            " ORDER BY year, quarter;";


        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:pulsefacility.db");
             PreparedStatement statement = connection.prepareStatement(sql);
        	 PreparedStatement statement2 = connection.prepareStatement(sql2);	
             ResultSet resultSet = statement.executeQuery();ResultSet resultSet2 = statement2.executeQuery()) {
        	
           

            reportBuilder.append("<table border='1'>");
            reportBuilder.append("<tr><th>Year</th><th>Quarter</th><th>Unique Experiment OSF Node Count</th></tr>");
            while (resultSet.next()) {
                reportBuilder.append("<tr>")
                        .append("<td>").append(resultSet.getString("year")).append("</td>")
                        .append("<td>").append(resultSet.getInt("quarter")).append("</td>")
                        .append("<td>").append(resultSet.getInt("unique_count")).append("</td>")
                        .append("</tr>");
            }
            
        
            reportBuilder.append("</table>");
            
            reportBuilder.append("<table border='1'>");
            reportBuilder.append("<tr><th>Year</th><th>Quarter</th><th>Unique OSF Name  Count</th></tr>");
            while (resultSet2.next()) {
                reportBuilder.append("<tr>")
                        .append("<td>").append(resultSet2.getString("year")).append("</td>")
                        .append("<td>").append(resultSet2.getInt("quarter")).append("</td>")
                        .append("<td>").append(resultSet2.getInt("user_count")).append("</td>")
                        .append("</tr>");
            }
            reportBuilder.append("</table>");
            
        } catch (Exception e) {
            e.printStackTrace();
            reportBuilder.append("<p>Error generating report: ").append(e.getMessage()).append("</p>");
        }
        System.out.println(reportBuilder.toString()); 
        return reportBuilder.toString();
    }
}
