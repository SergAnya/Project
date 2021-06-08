package com.bykov.project.conference.utils;

import com.bykov.project.conference.dto.FilterSortType;

import javax.servlet.http.HttpServletRequest;

public final class UtilsFilterSort {
    public static FilterSortType setFilterSortType(HttpServletRequest request) {
        return UtilsValidation.isNullOrEmpty(request.getParameter("sortType"))
                ? FilterSortType.ALL
                : FilterSortType.valueOf(request.getParameter("sortType").toUpperCase());
    }

    private UtilsFilterSort(){}
}
