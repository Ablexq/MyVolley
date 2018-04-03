package com.example.xuqiang.myvolley.retrofitrxjava;

import java.io.Serializable;
import java.util.List;

/**
 * @author android3
 * @date 2017/3/13
 */

public class RxHomeDataBean extends HttpResult implements Serializable {

    private int page_id;
    private String page_name;
    private AgreementBean agreement;
    private LogisticsTextBean logistics_text;
    private int is_collect;
    private List<PageListBean> page_list;

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public String getPage_name() {
        return page_name;
    }

    public void setPage_name(String page_name) {
        this.page_name = page_name;
    }

    public AgreementBean getAgreement() {
        return agreement;
    }

    public void setAgreement(AgreementBean agreement) {
        this.agreement = agreement;
    }

    public LogisticsTextBean getLogistics_text() {
        return logistics_text;
    }

    public void setLogistics_text(LogisticsTextBean logistics_text) {
        this.logistics_text = logistics_text;
    }

    public int getIs_collect() {
        return is_collect;
    }

    public void setIs_collect(int is_collect) {
        this.is_collect = is_collect;
    }

    public List<PageListBean> getPage_list() {
        return page_list;
    }

    public void setPage_list(List<PageListBean> page_list) {
        this.page_list = page_list;
    }

    public static class AgreementBean {
        /**
         * type :
         * id :
         */

        private String type;
        private String id;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class LogisticsTextBean {
        /**
         * type :
         * id :
         */

        private String type;
        private String id;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class PageListBean {
        /**
         * control_name : customList
         * control_data : {"is_main":1,"items":[{"main_title":"","url":{"type":"","id":""},"href_key":"","custom_pic":"http://ozhzgy2u6.bkt.clouddn.com/upload/common/1515984875748689.jpg","width":"800","height":"567"}]}
         * show_type : 1
         */

        private String control_name;
        private ControlDataBean control_data;
        private int show_type;

        public String getControl_name() {
            return control_name;
        }

        public void setControl_name(String control_name) {
            this.control_name = control_name;
        }

        public ControlDataBean getControl_data() {
            return control_data;
        }

        public void setControl_data(ControlDataBean control_data) {
            this.control_data = control_data;
        }

        public int getShow_type() {
            return show_type;
        }

        public void setShow_type(int show_type) {
            this.show_type = show_type;
        }

        public static class ControlDataBean {
            /**
             * is_main : 1
             * items : [{"main_title":"","url":{"type":"","id":""},"href_key":"","custom_pic":"http://ozhzgy2u6.bkt.clouddn.com/upload/common/1515984875748689.jpg","width":"800","height":"567"}]
             */

            private int is_main;
            private List<ItemsBean> items;

            public int getIs_main() {
                return is_main;
            }

            public void setIs_main(int is_main) {
                this.is_main = is_main;
            }

            public List<ItemsBean> getItems() {
                return items;
            }

            public void setItems(List<ItemsBean> items) {
                this.items = items;
            }

            public static class ItemsBean {
                /**
                 * main_title :
                 * url : {"type":"","id":""}
                 * href_key :
                 * custom_pic : http://ozhzgy2u6.bkt.clouddn.com/upload/common/1515984875748689.jpg
                 * width : 800
                 * height : 567
                 */

                private String main_title;
                private UrlBean url;
                private String href_key;
                private String custom_pic;
                private String width;
                private String height;

                public String getMain_title() {
                    return main_title;
                }

                public void setMain_title(String main_title) {
                    this.main_title = main_title;
                }

                public UrlBean getUrl() {
                    return url;
                }

                public void setUrl(UrlBean url) {
                    this.url = url;
                }

                public String getHref_key() {
                    return href_key;
                }

                public void setHref_key(String href_key) {
                    this.href_key = href_key;
                }

                public String getCustom_pic() {
                    return custom_pic;
                }

                public void setCustom_pic(String custom_pic) {
                    this.custom_pic = custom_pic;
                }

                public String getWidth() {
                    return width;
                }

                public void setWidth(String width) {
                    this.width = width;
                }

                public String getHeight() {
                    return height;
                }

                public void setHeight(String height) {
                    this.height = height;
                }

                public static class UrlBean {
                    /**
                     * type :
                     * id :
                     */

                    private String type;
                    private String id;

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }
                }
            }
        }
    }
}
