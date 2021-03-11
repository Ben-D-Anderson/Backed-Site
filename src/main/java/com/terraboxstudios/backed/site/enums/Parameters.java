package com.terraboxstudios.backed.site.enums;

public class Parameters {

    public enum ConfirmEmail implements baseParamEnum {
        CODE("code");

        private String param;

        ConfirmEmail(String param) {
            this.param = param;
        }

        @Override
        public String getParam() {
            return param;
        }
    }

    public enum Login implements baseParamEnum {
        USERNAME("username"),
        PASSWORD("password"),
        REMEMBER_ME("remember_me");

        private String param;

        Login(String param) {
            this.param = param;
        }

        @Override
        public String getParam() {
            return param;
        }
    }

    public enum Register implements baseParamEnum {
        USERNAME("username"),
        EMAIL("email"),
        PASSWORD("password"),
        PASSWORD_REPEAT("password_repeat");

        private String param;

        Register(String param) {
            this.param = param;
        }

        @Override
        public String getParam() {
            return param;
        }
    }

    public enum DownloadAPI implements baseParamEnum {
        FILENAME("filename");

        private String param;

        DownloadAPI(String param) {
            this.param = param;
        }

        @Override
        public String getParam() {
            return param;
        }
    }

    private interface baseParamEnum {
        String getParam();
    }

}
