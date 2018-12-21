package io.annot8.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.annot8.cli.Config.ProcessorSettingsPair;
import io.annot8.common.implementations.context.SimpleContext;
import io.annot8.common.implementations.data.BaseItemFactory;
import io.annot8.common.implementations.registries.ContentBuilderFactoryRegistry;
import io.annot8.common.pipelines.elements.Pipeline;
import io.annot8.common.pipelines.elements.PipelineBuilder;
import io.annot8.common.pipelines.queues.MemoryItemQueue;
import io.annot8.common.pipelines.simple.SimplePipelineBuilder;
import io.annot8.components.files.sources.FileSystemSource;
import io.annot8.components.files.sources.FileSystemSourceSettings;
import io.annot8.core.components.Processor;
import io.annot8.core.exceptions.Annot8Exception;
import io.annot8.core.settings.Settings;
import io.annot8.defaultimpl.content.DefaultText;
import io.annot8.defaultimpl.factories.DefaultBaseItemFactory;
import io.annot8.defaultimpl.factories.DefaultContentBuilderFactoryRegistry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Annot8 {

  public static void main(String[] args){
    if(args.length < 2){
      System.err.println("You must provide a configuration file and at least one directory");
      return;
    }

    // Read configuration from JSON file
    ObjectMapper objectMapper = new ObjectMapper();
    Config config;
    try {
      config = objectMapper.readValue(new File(args[0]), Config.class);
    } catch (IOException e) {
      System.err.println("Unable to parse configuration file");
      return;
    }

    //Initialize Annot8 environment
    ContentBuilderFactoryRegistry contentBuilderFactoryRegistry = new DefaultContentBuilderFactoryRegistry();
    contentBuilderFactoryRegistry.register(DefaultText.class, new DefaultText.BuilderFactory());

    BaseItemFactory bif = new DefaultBaseItemFactory(contentBuilderFactoryRegistry);

    PipelineBuilder builder = new SimplePipelineBuilder()
        .withName("Annot8 Pipeline")
        .withItemFactory(bif)
        .withQueue(new MemoryItemQueue());

    for(int i = 1; i < args.length; i++){
      Path p = Path.of(args[i]);
      builder.addSource(new FileSystemSource(), new FileSystemSourceSettings(p));
    }

    List<Settings> processorSettings = new ArrayList<>();

    for(ProcessorSettingsPair psp : config.getProcessorSettingsPairs()){
      Processor p;
      try {
        p = psp.getProcessor().getConstructor().newInstance();
      } catch (Exception e) {
        System.err.println("Couldn't find default constructor for processor "+psp.getProcessor().getName());
        continue;
      }

      builder.addProcessor(p);
      processorSettings.add(psp.getSettings());
    }

    Pipeline pipeline = builder.build();
    try {
      pipeline.configure(new SimpleContext(processorSettings));
    } catch (Annot8Exception e) {
      System.err.println("Unable to configure processors");
    }

    pipeline.run(); //FIXME: Why you not running the processors?!
  }

}
