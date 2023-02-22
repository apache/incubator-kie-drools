/**
 * Contains classes relevant to K-Opt moves.
 * A K-Opt move is a move that removes K edges,
 * and add K new edges that are composed of the removal edges endpoints.
 * The classes in this package implement the algorithms described in
 * <a href="http://webhotel4.ruc.dk/~keld/research/LKH/KoptReport.pdf">
 * "An Effective Implementation of K-opt Moves for the Lin-Kernighan TSP Heuristic"
 * </a>.
 * <br />
 * The paper changes the problem of performing a K-Opt
 * to the problem of transforming a signed permutation that consists of a single K-cycle to the identity permutation.
 * A signed permutation is a permutation where each element have a sign.
 * Let the removed edges be (t_1, t_2), (t_3, t_4), ..., (t_(2k - 1), t_2k).
 * Let s_1, s_2, ..., s_k be segments from the original tour that starts and end at two different endpoints of two different
 * removed edges.
 * Additionally, let s_1, s_2, ..., s_k be sorted such that
 * <ul>
 * <li>s_1 starts at t_2</li>
 * <li>s_2 starts at the successor of the end of s_1</li>
 * <li>in general s_i starts at the successor of s_(i-1)</li>
 * <li>s_k ends at t_1</li>
 * </ul>
 * For example, if the original tour was [t_1, t_2, t_4, t_3, t_8, t_7, t_5, t_6],
 * then
 * <ul>
 * <li>s_1 = t_2...t_4</li>
 * <li>s_2 = t_3...t_8</li>
 * <li>s_3 = t_7...t_5</li>
 * <li>s_4 = t_6...t_1</li>
 * </ul>
 * These segments will still be in the tour after the K-Opt move is performed, but may be reversed and in a different order.
 * To construct the signed permutation P corresponding to the K-Opt, create a list of K elements where
 * <ul>
 * <li>
 * abs(P[i]) = starting from t_2, he number of segments needed to be transversed to encounter s[i] + 1
 * (after the K-Opt move been applied)
 * </li>
 * <li>
 * sign(P[i]) = +1 if s[i] has the same orientation in the tour after the K-Opt move been applied,
 * -1 if it been reversed
 * </li>
 * </ul>
 * For example, if the original tour was <br />
 * [t_2, t_4, t_3, t_8, t_7, t_5, t_6, t_1] <br />
 * and the 4-Opt move changes it to <br />
 * [t2, t_4, t_5, t_7, t_6, t_1, t_8, t_3] <br />
 * Then
 * <ul>
 * <li>s_1 = (t_2...t_4), s_2 = (t_3...t_8), s_3 = (t_7...t_5), s_4 = (t_6...t_1)</li>
 * <li>P_1 = +1 (first segment encountered, same orientation as original</li>
 * <li>P_2 = -4 ((t_8...t_3) is encountered last in the new tour, and it was reversed from (t_3...t_8))</li>
 * <li>P_3 = -2 ((t_5...t_7) is second segment encountered in the new tour, and it was reversed from (t_7...t_5)</li>
 * <li>P_4 = +3 ((t_6...t_1) is the third segment encountered in the new tour, and has the same orientation.</li>
 * </ul>
 * Thus, for this example, P = (+1, -4, -2, +3).
 * The goal is to transform P into the identity permutation (+1, +2, +3, +4) using the minimal amount of signed reversals.
 * For the above example, it can be achieved using three signed reversals:
 *
 * <ol>
 * <li>(+1 -4 [-2 +3])</li>
 * <li>(+1 [-4 -3 +2])</li>
 * <li>(+1 [-2] +3 +4)</li>
 * <li>(+1, +2, +3, +4)</li>
 * </ol>
 *
 * Each signed reversal directly corresponds to a 2-opt move (which reverses a sub-path of the tour). For the
 * above signed reversals, they are:
 *
 * <ol>
 * <li>2-opt(t2, t1, t8, t7)</li>
 * <li>2-opt(t4, t3, t2, t7)</li>
 * <li>2-opt(t7, t4, t5, t6)</li>
 * </ol>
 *
 * Where 2-opt(a, b, c, d) remove edges (a, b) and (c, d) and add edges (b, c), (a, d).
 * The series of signed reversals can be found by the algorithm described in
 * <a href="https://dl.acm.org/doi/pdf/10.1145/300515.300516">
 * "Transforming Cabbage into Turnip: Polynomial Algorithm for Sorting Signed Permutations by Reversals"
 * </a>.
 */
package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;