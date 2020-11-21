package com.wfm.soundcollaborations.webservice.dtos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositionDemo {


    private static final Long COMP_1_ID = 1L;
    private static final Long COMP_2_ID = 2L;
    private static final Long COMP_3_ID = 3L;

    public static Map<Long, CompositionResponse> compositions() {

        SoundResponse c1s1 = new SoundResponse(1L, 0, "https://res.cloudinary.com/hby9r5zna/video/upload/v1605963256/klangfang/prod/5f432b523b624f35b1cbd284ccd69477.3gp", 1100, 1000, "creatorName");
        List<SoundResponse> c1sounds = Arrays.asList(c1s1);
        CompositionResponse comp1 = new CompositionResponse(COMP_1_ID, "title", "creatorName", "time", "status",1, 10, "https://res.cloudinary.com/hby9r5zna/video/upload/v1605963256/klangfang/prod/5f432b523b624f35b1cbd284ccd69477.3gp", c1sounds);

        SoundResponse c2s1 = new SoundResponse(1L, 0, "https://res.cloudinary.com/hby9r5zna/video/upload/v1605963256/klangfang/prod/5f432b523b624f35b1cbd284ccd69477.3gp", 1100, 1000, "creatorName");
        List<SoundResponse> c2sounds = Arrays.asList(c2s1);
        CompositionResponse comp2 = new CompositionResponse(COMP_2_ID, "title", "creatorName", "time", "status",1, 10, "https://res.cloudinary.com/hby9r5zna/video/upload/v1605963256/klangfang/prod/5f432b523b624f35b1cbd284ccd69477.3gp", c2sounds);

        SoundResponse c3s1 = new SoundResponse(1L, 0, "https://res.cloudinary.com/hby9r5zna/video/upload/v1605963256/klangfang/prod/5f432b523b624f35b1cbd284ccd69477.3gp", 1100, 1000, "creatorName");
        List<SoundResponse> c3sounds = Arrays.asList(c3s1);
        CompositionResponse comp3 = new CompositionResponse(COMP_3_ID, "title", "creatorName", "time", "status",1, 10, "https://res.cloudinary.com/hby9r5zna/video/upload/v1605963256/klangfang/prod/5f432b523b624f35b1cbd284ccd69477.3gp", c3sounds);


        Map<Long, CompositionResponse> compositions = new HashMap<>();
        compositions.put(COMP_1_ID, comp1);
        compositions.put(COMP_2_ID, comp2);
        compositions.put(COMP_3_ID, comp3);

        return compositions;

    }


    public static OverviewResponse overviewResponse() {

        Map<Long, CompositionResponse> compositions = compositions();
        CompositionResponse comp1 = compositions.get(COMP_1_ID);
        CompositionResponse comp2 = compositions.get(COMP_2_ID);
        CompositionResponse comp3 = compositions.get(COMP_3_ID);

        CompositionOverviewResp compOw1 = new CompositionOverviewResp(comp1.id, comp1.title, comp1.numberOfMembers, comp1.snippet);
        CompositionOverviewResp compOw2 = new CompositionOverviewResp(comp2.id, comp2.title, comp2.numberOfMembers, comp2.snippet);
        CompositionOverviewResp compOw3 = new CompositionOverviewResp(comp3.id, comp3.title, comp3.numberOfMembers, comp3.snippet);

        return new OverviewResponse(Arrays.asList(compOw1, compOw2, compOw3));

    }

}