package com.royal.recreation.controller.base;

import com.royal.recreation.config.bean.MyUserDetails;
import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.core.type.UserType;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.ConvertUtil;
import com.royal.recreation.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.royal.recreation.util.DateUtil.QUERY_DATE_TIME_FORMATTER;
import static com.royal.recreation.util.DateUtil.QUERY_DATE__FORMATTER;

public class BaseController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    public Mongo getBaseMongoQuery(MyUserDetails userDetails, BaseQuery baseQuery, Model model) {
        Mongo query = Mongo.buildMongo();
        queryOfUser(query, userDetails, baseQuery, model);
        queryOfTime(query, baseQuery);
        return query;
    }

    public void queryOfUser(Mongo query, MyUserDetails userDetails, BaseQuery baseQuery, Model model) {
        String username = baseQuery.getUsername();
        if (StringUtils.isEmpty(username)) {
            query.eq("userId", userDetails.getId());
        } else {
            model.addAttribute("username", baseQuery.getUsername());
            UserInfo pUser = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
            UserInfo userInfo = Mongo.buildMongo().eq("username", baseQuery.getUsername()).findOne(UserInfo.class);
            if (userInfo == null
                    // 等级不够
                    || (pUser.getUserType().ordinal() >= userInfo.getUserType().ordinal())) {
                // 无权限的情况
                query.id("111111111111111111111111");
            } else {
                query.eq("userId", userInfo.getId());
            }
        }
    }

    public void queryOfDate(Mongo query, BaseQuery baseQuery) {
        String fromDate = baseQuery.getFromDate();
        String toDate = baseQuery.getToDate();
        if (!StringUtils.isEmpty(fromDate) && !StringUtils.isEmpty(toDate)) {
            query.between("time", DateUtil.dateToTime(LocalDate.parse(fromDate, QUERY_DATE__FORMATTER)), DateUtil.dateToTime(LocalDate.parse(toDate, QUERY_DATE__FORMATTER)), Mongo.Between.EQ);
        } else {
            if (!StringUtils.isEmpty(fromDate)) {
                query.gte("time", DateUtil.dateToTime(LocalDate.parse(fromDate, QUERY_DATE__FORMATTER)));
            }
            if (!StringUtils.isEmpty(toDate)) {
                query.lte("time", DateUtil.dateToTime(LocalDate.parse(toDate, QUERY_DATE__FORMATTER)));
            }
        }
    }

    public void queryOfTime(Mongo query, BaseQuery baseQuery) {
        String fromTime = baseQuery.getFromTime();
        String toTime = baseQuery.getToTime();
        if (!StringUtils.isEmpty(fromTime) && !StringUtils.isEmpty(toTime)) {
            query.between("createAt", LocalDateTime.parse(fromTime, QUERY_DATE_TIME_FORMATTER), LocalDateTime.parse(toTime, QUERY_DATE_TIME_FORMATTER), Mongo.Between.EQ);
        } else {
            if (!StringUtils.isEmpty(fromTime)) {
                query.gte("createAt", LocalDateTime.parse(fromTime, QUERY_DATE_TIME_FORMATTER));
            }
            if (!StringUtils.isEmpty(toTime)) {
                query.lte("createAt", LocalDateTime.parse(toTime, QUERY_DATE_TIME_FORMATTER));
            }
        }
    }

    public Object responseError(String errorMsg) {
        try {
            this.response.setHeader("X-Error-Message-Times", "1");
            this.response.setHeader("X-Error-Message", URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String getQueryString(String prefix) {
        prefix += "?";
        String queryString = request.getQueryString();
        int page = ConvertUtil.Int(request.getParameter("page"), 1);
        int pageType = ConvertUtil.Int(request.getParameter("pageType"), 0);
        if (queryString == null) {
            return prefix + "page=1&pageType=";
        }
        int pageIndex = queryString.indexOf("page");
        if (pageIndex != -1) {
            if (pageIndex == 0) {
                queryString = queryString.substring(0, pageIndex);
            } else {
                queryString = queryString.substring(0, pageIndex - 1);
            }
        }
        return prefix + queryString + "&page=" + (page + pageType) + "&pageType=";
    }

}
