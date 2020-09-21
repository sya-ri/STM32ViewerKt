package me.syari.stm32.viewer.ui

import tornadofx.*

class PluginOptionViewCode : View("プラグイン オプション") {
    override val root = vbox {
        prefHeight = 250.0
        prefWidth = 600.0

        hbox {
            label("CubeIDE") {
                prefHeight = 30.0
                prefWidth = 80.0
                hboxConstraints {
                    marginLeft = 20.0
                }
            }
            textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
            button("開く") {
                prefHeight = 30.0
                prefWidth = 50.0
                hboxConstraints {
                    marginLeftRight(20.0)
                }
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }

        hbox {
            label("ST-Link GDB Server") {
                prefHeight = 30.0
                prefWidth = 150.0
                hboxConstraints {
                    marginLeft = 20.0
                }
            }
            textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }

        hbox {
            label("CubeProgrammer") {
                prefHeight = 30.0
                prefWidth = 150.0
                hboxConstraints {
                    marginLeft = 20.0
                }
            }
            textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }

        hbox {
            label("GNU Arm Embedded") {
                prefHeight = 30.0
                prefWidth = 150.0
                hboxConstraints {
                    marginLeft = 20.0
                }
            }
            textfield {
                prefHeight = 30.0
                prefWidth = 400.0
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }

        hbox {
            button("キャンセル") {
                prefHeight = 30.0
                prefWidth = 100.0
                hboxConstraints {
                    marginLeft = 150.0
                    marginRight = 50.0
                }
            }
            button("保存") {
                prefHeight = 30.0
                prefWidth = 100.0
                hboxConstraints {
                    marginLeft = 50.0
                    marginRight = 150.0
                }
            }
            vboxConstraints {
                marginTopBottom(10.0)
            }
        }
    }
}
