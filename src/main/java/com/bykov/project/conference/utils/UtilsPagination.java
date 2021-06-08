package com.bykov.project.conference.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public final class UtilsPagination {
    public static Map<String, Integer> calcPaginationParameters(HttpServletRequest request, int rows) {
        int currentPage = Integer.parseInt(getOrDefault(request, "currentPage", "1"));
        int recordsPerPage = Integer.parseInt(getOrDefault(request, "recordsPerPage", "5"));

        int pagesAmount = rows / recordsPerPage;
        if (rows % recordsPerPage > 0) {
            pagesAmount++;
        }
        if (currentPage > pagesAmount) {
            currentPage = pagesAmount;
        }
        int begin = currentPage * recordsPerPage - recordsPerPage;
        if (begin < 0) {
            begin = 0;
        }

        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("currentPage", currentPage);
        parameters.put("recordsPerPage", recordsPerPage);
        parameters.put("pagesAmount", pagesAmount);
        parameters.put("begin", begin);

        return parameters;
    }

    private static String getOrDefault(HttpServletRequest request, String param, String byDefault) {
        return request.getParameter(param) != null
                ? request.getParameter(param)
                : byDefault;
    }

    private UtilsPagination(){}
}
