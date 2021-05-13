package com.chinasoft.gangjiantou.constant;


import com.github.wangran99.welink.api.client.openapi.model.IException;

public enum ResultCode implements IException {

    /**************************
     * 使用统一的错误码
     * 修改后注意同步到其他工程中
     *************************/

    SUCCESS(0, "Success."),
    ErrorOperationFailed(1,"Operation failed."),
    AuthFailedOrExpired(2,"user authorization failed or expired."),

    RequestLimitException(1001,"request too frequent,please wait a moment."),

    //search parameter error
    SEARCH_OFFSET_OR_LIMIT_EMPTY(56120, "the parameter offset or limit cannot be null"),
    SEARCH_DATE_RANGE_TOO_LONG(56121, "te date time range has exceeded the limit"),
    SEARCH_DATE_FORMAT_WRONG(56122, "the date format is not right"),
    SEARCH_DATEFROM_OR_DATETO_EMPTY(56123, "the parameter dateFrom or dateTo cannot be null"),
    SEARCH_DATE_UPSIDEDOWN(56124, "dateFrom/startTime cannot be late than dateTo/endTime"),
    SEARCH_USERIDS_TOO_LARGE(56125, "the size of userIds has exceed the limit"),

    //公共结果码：1010xx
    ErrorRequestInvalid(101000,"Request is invalid."),
    ErrorAuthorization(101001,"Authorization is failed."),
    ErrorInvalidParameter(101002, "Invalid parameter."),
    ErrorNoPermission(101003, "No operation permission."),
    ErrorNoAuthToken(101004, "Authorization token is required and can not be null."),
    ErrorLongitudeOrLatitudeCannotBeNull(101006, "Longitude or latitude can not be null"),
    ErrorGetUserRoleFailedDueToBadNetwork(101005, "Get user role list failed due to the interface network exception"),
    ErrorGetUserRoleFailedDueToTokenExpired(101006, "Get user role list failed due to the token is invalid or expired, a re-login is needed."),
    ErrorDateFormatError(101007, "Date format error"),
    ErrorDateEmpty(101008, "The parameter dateFrom/dateTo or startTime/endTime can not be null."),
    ErrorSearchDateUpsideDown(101009, "dateFrom/startTime cannot be late than dateTo/endTime"),
    ErrorSearchDateRangeTooLong(101010, "the date time range has exceeded the limit"),
    FailValidateGeoFence(101011, "Longitude or latitude validate GeoFence failed"),
    ERROREXPORTFILE(101012, "export attendance file failed"),

    //面向租户管理员业务的结果码101100-101299：
    ErrorYouExportNothing(101100, "You export nothing, at least one department or one person selected"),
    ErrorDataHasBeenModifiedOrRemoved(101105, "The data has been modified or removed, update failed."),
    ErrorPunchTimesExceed2SomeFunctionsNotSupported(101106, "when the punchTimes value exceeds 2, hasLunchBreak/enableArriveLateLeaveLate is not supported."),
    ErrorPunchTimesShouldMatchTimeSettings(101107, "the punchTimes value should match the size of punchTimeList"),

    //面向普通用户业务的结果码：101300-101499
    ErrorNoAuthToken2(101300, "No auth token"), //与101004重复，为了保持原来接口报错不变，保留这个结果码
    ErrorDeleteTodoTaskFailed(101301, "Delete todo task failed"),

    NoAdminPermission(1001300, "You don't have enterprise admin permission."), //本意应该是要设置101300，但写错了？...有接口使用了，且如修改为101300也会有冲突，不修改

    ErrorSystemBusy(-1, "System is busy now.");


    private int code;
    private String desc;

    private ResultCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        return "Exception { code=" + this.code + ", desc=" + this.desc + "}";
    }
}