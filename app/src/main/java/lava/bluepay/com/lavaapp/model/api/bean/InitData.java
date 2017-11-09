package lava.bluepay.com.lavaapp.model.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bluepay on 2017/10/20.
 */

public class InitData extends BaseBean {
    /**
     * data : {"time":"03:10:00,10:00:00","switch":"1","sub_key_sms":"R 01","shorcode":"4533059","content":"ชื่อบริการ\n                \\n-- nicetime\n                \\nรายละเอียด\n                \\nเป็นบริการรายวันประเภท วีดีโอสาวสวย ทุกๆวันจะส่งเป็นUrlให้ทางข้อความ วันละ 1 ข้อความ กรุณาเชื่อมต่ออินเทอร์เน็ตในการดาวน์โหลด\n                \\n-- อัตราค่าบริการ 9 บาท/ข้อความ, 1 ข้อความ/วัน (ไม่รวมภาษีมูลค่าเพิ่ม)\n                \\n-- สมัครบริการ พิมพ์ R 05 ส่งมาที่ 4533024\n                \\n-- ยกเลิกบริการ พิมพ์ C 05 ส่งมาที่ 4533024\n                \\n-- สอบถามโทร 02-033-0075 เวลา 8.30-17.30น. (จันทร์-ศุกร์)\n                \\n-- บริการนี้สำหรับผู้ใช้บริการที่มีอายุ 18 ปีขึ้นไปเท่านั้น","upgrade":0}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * time : 03:10:00,10:00:00
         * switch : 1
         * sub_key_sms : R 01
         * shorcode : 4533059
         * content : ชื่อบริการ
         \n-- nicetime
         \nรายละเอียด
         \nเป็นบริการรายวันประเภท วีดีโอสาวสวย ทุกๆวันจะส่งเป็นUrlให้ทางข้อความ วันละ 1 ข้อความ กรุณาเชื่อมต่ออินเทอร์เน็ตในการดาวน์โหลด
         \n-- อัตราค่าบริการ 9 บาท/ข้อความ, 1 ข้อความ/วัน (ไม่รวมภาษีมูลค่าเพิ่ม)
         \n-- สมัครบริการ พิมพ์ R 05 ส่งมาที่ 4533024
         \n-- ยกเลิกบริการ พิมพ์ C 05 ส่งมาที่ 4533024
         \n-- สอบถามโทร 02-033-0075 เวลา 8.30-17.30น. (จันทร์-ศุกร์)
         \n-- บริการนี้สำหรับผู้ใช้บริการที่มีอายุ 18 ปีขึ้นไปเท่านั้น
         * upgrade : 0
         */

        private String time;
        @SerializedName("switch")
        private int switchX;
        private String sub_key_sms;
        private String shorcode;
        private String content;
        private int upgrade;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getSwitchX() {
            return switchX;
        }

        public void setSwitchX(int switchX) {
            this.switchX = switchX;
        }

        public String getSub_key_sms() {
            return sub_key_sms;
        }

        public void setSub_key_sms(String sub_key_sms) {
            this.sub_key_sms = sub_key_sms;
        }

        public String getShorcode() {
            return shorcode;
        }

        public void setShorcode(String shorcode) {
            this.shorcode = shorcode;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getUpgrade() {
            return upgrade;
        }

        public void setUpgrade(int upgrade) {
            this.upgrade = upgrade;
        }
    }
}
