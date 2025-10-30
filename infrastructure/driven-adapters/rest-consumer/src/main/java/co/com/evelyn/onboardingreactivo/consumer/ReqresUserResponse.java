package co.com.evelyn.onboardingreactivo.consumer;

import lombok.Data;

@Data
public class ReqresUserResponse {
    private ReqresUser data;

    @Data
    public static class ReqresUser {
        private Integer id;
        private String email;
        private String first_name;
        private String last_name;
        private String avatar;
    }
}
