package io.annot8.cli;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.annot8.core.components.Processor;
import io.annot8.core.settings.EmptySettings;
import io.annot8.core.settings.Settings;
import java.util.ArrayList;
import java.util.List;

public class Config {
  @JsonProperty("processors")
  private List<ProcessorSettingsPair> processorSettingsPairs = new ArrayList<>();

  public List<ProcessorSettingsPair> getProcessorSettingsPairs() {
    return processorSettingsPairs;
  }

  public void setProcessorSettingsPairs(List<ProcessorSettingsPair> processorSettingsPairs) {
    this.processorSettingsPairs = processorSettingsPairs;
  }

  public void addProcessorSettingsPair(ProcessorSettingsPair processorSettingsPair){
    this.processorSettingsPairs.add(processorSettingsPair);
  }

  public void addProcessor(Class<Processor> processor){
    this.processorSettingsPairs.add(new ProcessorSettingsPair(processor, EmptySettings.getInstance()));
  }


  public static class ProcessorSettingsPair {
    private Class<? extends Processor> processor;
    private Settings settings;

    public ProcessorSettingsPair(){
      // Default constructor
    }

    public ProcessorSettingsPair(Class<? extends Processor> processor, Settings settings){
      this.processor = processor;
      this.settings = settings;
    }

    public Class<? extends Processor> getProcessor() {
      return processor;
    }

    public void setProcessor(Class<? extends Processor> processor) {
      this.processor = processor;
    }

    public Settings getSettings() {
      return settings;
    }

    public void setSettings(Settings settings) {
      this.settings = settings;
    }
  }
}
