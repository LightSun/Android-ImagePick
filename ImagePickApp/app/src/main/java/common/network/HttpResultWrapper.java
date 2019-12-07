package common.network;

import java.util.List;

/**
 * Created by Administrator on 2017/8/26.
 */

public class HttpResultWrapper<T> {


    /**
     * pageNo : 1
     * pageSize : 20
     * count : 100
     * list : [{"id":"1","capacityNo":"YL201707170003","startName":"吉林四平","endName":"四川宜宾","endTime":"2017-5-24","capacity":"1000.00"},{"id":"2","capacityNo":"YL201707170004","startName":"吉林四平","endName":"四川宜宾","endTime":"2017-5-24","capacity":"1000.00"}]
     */

    private int pageNo;
    private int pageSize;
    private int count;
    private List<T> list;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
