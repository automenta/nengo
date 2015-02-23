"""A series of Python commands to execute when starting up
the graphical front-end to Nengo."""

execfile('python/startup_common.py')

from ca.nengo.ui.configurable.descriptors import PString
from ca.nengo.ui.configurable.descriptors import PInt
from ca.nengo.ui.configurable.descriptors import PFloat
from ca.nengo.ui.configurable.descriptors import PLong
from ca.nengo.ui.configurable.descriptors import PBoolean
from ca.nengo.ui.configurable import IConfigurable

import toolbar
import template

__nengo_ui__ = True
