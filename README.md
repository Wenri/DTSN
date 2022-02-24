# Deterministic-and-Stochastic-Synthesis-Network

[[paper]](https://arxiv.org/abs/1809.06557) [[supp]](assets/supp.pdf) [[demo]](demo/SRAPP.pdf)

Codes and models for the SIGGRAPH Asia 2018 paper "Image Super-Resolution via Deterministic-Stochastic Synthesis and Local Statistical Rectification".

## Deterministic and Stochastic Synthesis

![](assets/teaser.png)

By [Weifeng Ge](https://i.cs.hku.hk/~wfge/), [Bingchen Gong](https://github.com/Wenri), [Yizhou Yu](http://i.cs.hku.hk/~yzyu/)

Department of Computer Science, The University of Hong Kong

### Table of Contents
1. [Introduction](#introduction)
2. [Citation](#citation)
3. [Pipeline](#pipeline)
4. [Codes and Installation](#codes-and-installation)
5. [Models](#models)
6. [Results](#results)

### Introduction

This repository contains the codes and models described in the paper ["Image Super-Resolution via 
Deterministic-Stochastic Synthesis and Local Statistical Rectification"](https://arxiv.org/abs/1809.06557). 
These models are trained and tested on the dataset of [NTIRE 2017](http://www.vision.ee.ethz.ch/ntire17/) 
DIV2K super resolution track.

**Note**

1. All algorithms are implemented based on the deep learning framework [Caffe](https://github.com/BVLC/caffe).
2. Please add the additional layers used into your own Caffe to run the training codes.

### Citation

If you use these codes and models in your research, please cite:

    @inproceedings{ge2018image,
           title={Image super-resolution via deterministic-stochastic synthesis and local statistical rectification},
           author={Ge, Weifeng and Gong, Bingchen and Yu, Yizhou},
           booktitle={SIGGRAPH Asia 2018 Technical Papers},
           pages={260},
           year={2018},
           organization={ACM}
    }
