
# Advanced Embedded Linux Development - ECEA 5305-5307 final project

This repo is Yocto part of the project for reciver and indicator board - Raspberry Pi 4B

The Yocto part of the project  for sender board - Raspberry Pi Zero 2 WH is [here](https://github.com/vizdr/final-project-assignment-vladzdravkov) 

## Project Overview 

Wiki Page with the entire project overview can be found
[here](https://github.com/cu-ecen-aeld/final-project-vizdr/wiki)


## Project Goals

- Detect sounds which exceed the adjustable threschold and respond using module with LED matrix on Raspberry Pi 4B  board and sensor on Raspberry Pi zero 2 board.


- Enable **communication** using the **CAN protocol** with **RS485 / MCP2515 SPI modules**.


- Control of Sound Sensor and of LED module with **GPIO** on the Raspberry Pi Zero and 4B correspondingly.


- Implement apllication part with **state-machine based system** triggered by remote-side sound detection.


- Utilize **Yocto** to generate a lightweight Linux image with integrated kernel drivers and services.


## Source Code Organization

Yocto part for the sender board - Raspberry Pi Zero 2 WH can be found [here](https://github.com/vizdr/final-project-assignment-vladzdravkov) 

Yocto part for the receiver board - Raspberry Pi 4B is the current [repo](https://github.com/cu-ecen-aeld/final-project-vizdr)

Application part will be placed in the separate [repo](https://github.com/vizdr/final-project-assignment-app-vizdr) 


## Project Member

Vladimir Ilia Zdravkov 
vladzdravkov@gmail.com


## Schedule Page

An entire schedule and milestones for the project can be found [here](https://github.com/users/vizdr/projects/1).