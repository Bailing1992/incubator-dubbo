/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.cluster;

import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.support.FailoverCluster;

/**
 * Cluster. (SPI, Singleton, ThreadSafe)
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Computer_cluster">Cluster</a>
 * <a href="http://en.wikipedia.org/wiki/Fault-tolerant_system">Fault-Tolerant</a>
 *
 *
 *  Dubbo 提供了多种 集群实现，包含但不限于 Failover Cluster、Failfast Cluster 和 Failsafe Cluster 等
 *
 *
 *  集群的作用：
 *    集群 Cluster 用途 是将多个服务提供者 合并 为一个 Cluster Invoker，并将这个 Invoker 暴露给服务消费者。
 *    这样一来，服务消费者只需通过这个 Invoker 进行远程调用即可，至于具体调用哪个服务提供者，以及调用失败后如何处理等问题，
 *    现在都交给集群模块去处理。集群模块是服务提供者和服务消费者的中间层，为服务消费者屏蔽了服务提供者的情况，
 *    这样服务消费者就可以专心处理远程调用相关事宜。比如发请求，接受服务提供者返回的数据等
 *
 *
 * 集群 工作过程 可分为 两个阶段:
 *   第一个阶段是 在服务消费者初始化期间，集群 Cluster 实现类 为 服务消费者 创建 Cluster Invoker 实例，即 merge 操作
 *   第二个阶段是 在服务消费者 进行远程调用时。以 FailoverClusterInvoker 为例，
 *     该类型 Cluster Invoker 首先会调用 Directory 的 list 方法列举 Invoker 列表（可将 Invoker 简单理解为服务提供者）。
 *     Directory 的用途是保存 Invoker，可简单类比为 List<Invoker>。
 *     其 实现类 RegistryDirectory 是一个 动态服务目录，可感知 注册中心配置 的变化，
 *     它所持有的 Invoker 列表会随着注册中心内容的变化而变化。
 *     每次变化后，RegistryDirectory 会动态增删 Invoker，并调用 Router 的 route 方法进行路由，过滤掉不符合路由规则的 Invoker。
 *     当 FailoverClusterInvoker 拿到 Directory 返回的 Invoker 列表后，它会通过 LoadBalance 从 Invoker 列表中选择一个 Invoker。
 *     最后 FailoverClusterInvoker 会将参数传给 LoadBalance 选择出的 Invoker 实例的 invoke 方法，进行真正的远程调用。
 *
 */
@SPI(FailoverCluster.NAME)
public interface Cluster {

    /**
     * Merge the directory invokers to a virtual invoker.
     *
     * @param <T>
     * @param directory
     * @return cluster invoker
     * @throws RpcException
     */
    @Adaptive
    <T> Invoker<T> join(Directory<T> directory) throws RpcException;

}