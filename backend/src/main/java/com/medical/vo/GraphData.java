package com.medical.vo;

import lombok.Data;
import java.util.List;

/**
 * 图谱数据VO
 */
@Data
public class GraphData {
    private List<GraphNode> nodes;
    private List<GraphLink> links;
}
