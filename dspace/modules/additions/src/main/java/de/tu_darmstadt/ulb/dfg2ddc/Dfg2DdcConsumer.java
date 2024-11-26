/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package de.tu_darmstadt.ulb.dfg2ddc;

import java.util.ArrayList;
import java.util.List;

import de.tu_darmstadt.ulb.record.RecordService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Consumer;
import org.dspace.event.Event;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * @author Qin Zhao (ULB Darmstadt)
 * @version 05.11.2024
 */
public class Dfg2DdcConsumer implements Consumer {
    private static Logger log = org.apache.logging.log4j.LogManager.getLogger(Dfg2DdcConsumer.class);

    private RecordService dfg2DdcService;

    @Override
    public void initialize() throws Exception {
        dfg2DdcService = DSpaceServicesFactory.getInstance().getServiceManager().getServiceByName(
            "de.tu_darmstadt.ulb.record.RecordService", RecordService.class);
    }

    @Override
    public void consume(Context ctx, Event event) throws Exception {
        if (event.getSubjectType() != Constants.ITEM) {
            log.warn("Dfg2DdcConsumer should not have been given this kind of subject in an event, skipping: "
                         + event.toString());
            return;
        }
        DSpaceObject dso = event.getSubject(ctx);
        if (!(dso instanceof Item)) {
            log.debug("Dfg2DdcConsumer got an event whose subject was not an item, skipping: " + event.toString());
            return;
        }
        Item item = (Item) dso;
        ItemService itemService = ContentServiceFactory.getInstance().getItemService();
        // clean ddc in metadata
        itemService.clearMetadata(ctx, item, "dc", "subject", "ddc", Item.ANY);
        List<MetadataValue> mdList = itemService.getMetadata(item, "dc", "subject",
                                                             "classification", Item.ANY);
        List<String> ddcList = new ArrayList<String>();
        // get mapped ddc
        for (MetadataValue md : mdList) {
            String dfg = md.getValue();
            if (StringUtils.isNotBlank(dfg)) {
                String ddc = dfg2DdcService.getValue(dfg);
                if (!ddcList.contains(ddc)) {
                    ddcList.add(ddc);
                }
            }
        }
        // add ddc in metadata
        itemService.addMetadata(ctx, item, "dc", "subject", "ddc", null, ddcList);
        log.info("Dfg2DdcConsumer cleanup and added new mapped ddc in metadata: " + String.join(",", ddcList));
    }

    @Override
    public void end(Context ctx) throws Exception {
    }

    @Override
    public void finish(Context ctx) throws Exception {
    }
}
