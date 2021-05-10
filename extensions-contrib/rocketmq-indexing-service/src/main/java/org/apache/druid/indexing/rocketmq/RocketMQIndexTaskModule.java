/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.indexing.rocketmq;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import org.apache.druid.guice.LazySingleton;
import org.apache.druid.indexing.rocketmq.supervisor.RocketMQSupervisorSpec;
import org.apache.druid.indexing.rocketmq.supervisor.RocketMQSupervisorTuningConfig;
import org.apache.druid.indexing.seekablestream.SeekableStreamIndexTaskClientFactory;
import org.apache.druid.initialization.DruidModule;

import java.util.List;

public class RocketMQIndexTaskModule implements DruidModule
{
  @Override
  public List<? extends Module> getJacksonModules()
  {
    return ImmutableList.of(
        new SimpleModule(getClass().getSimpleName())
            .registerSubtypes(
                new NamedType(RocketMQIndexTask.class, "index_rocketmq"),
                new NamedType(RocketMQDataSourceMetadata.class, "rocketmq"),
                new NamedType(RocketMQIndexTaskIOConfig.class, "rocketmq"),
                // "RocketMQTuningConfig" is not the ideal name, but is needed for backwards compatibility.
                // (Older versions of Druid didn't specify a type name and got this one by default.)
                new NamedType(RocketMQIndexTaskTuningConfig.class, "RocketMQTuningConfig"),
                new NamedType(RocketMQSupervisorTuningConfig.class, "rocketmq"),
                new NamedType(RocketMQSupervisorSpec.class, "rocketmq"),
                new NamedType(RocketMQSamplerSpec.class, "rocketmq")
            )
    );
  }

  @Override
  public void configure(Binder binder)
  {
    binder.bind(
        new TypeLiteral<SeekableStreamIndexTaskClientFactory<RocketMQIndexTaskClient>>()
        {
        }
    ).to(RocketMQIndexTaskClientFactory.class).in(LazySingleton.class);
  }
}
